package org.acme.git.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GitStorageService implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitStorageService.class);

	private final Path rootLocation;
	private final String remoteUrl;
	private final Lock directoryLock;
    private final int lockTimeoutSeconds;

	private UsernamePasswordCredentialsProvider user;
    private Git git;

	@Autowired
	public GitStorageService(StorageProperties properties) {
        if(properties.getLocation().trim().length() == 0){
            throw new StorageException("File upload location can not be Empty."); 
		}
		if(properties.getRemoteUrl().trim().length() == 0){
            throw new StorageException("Push repository url can not be Empty."); 
		}
		this.rootLocation = Paths.get(properties.getLocation());
		this.remoteUrl = properties.getRemoteUrl();
		this.directoryLock = new ReentrantLock();
		this.lockTimeoutSeconds = 90;
		user = new UsernamePasswordCredentialsProvider("root", "P@ssw0rd");
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
				git.add().addFilepattern(".").call();
			}
		}
		catch (IOException | GitAPIException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public void commit(String message) {
		try{
			git.commit().setMessage(message).call();
			PushCommand pushCommand = git.push();
			pushCommand.setCredentialsProvider(user);
			pushCommand.setRemote("origin").call();
			LOGGER.info("committed message = " + message);
		}catch(GitAPIException e){
			throw new StorageException("Failed to commit.", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
			final File gitDirectory = new File(rootLocation.toFile(), ".git");
			LOGGER.info(
				"Initializing repository "
						+ remoteUrl
						+ " in working dir "
						+ rootLocation);
			
			 synchronizedOperation(
				new Callable<Void>() {
					@Override
					public Void call() {
						try {
							if (!gitDirectory.exists()) {
								if (checkRemoteExist()) {
									LOGGER.info("Local repository not found, creating a new clone...");
									git = cloneRepository();
								} else {
									LOGGER.error("Remote repository not found...");
								}
							} else {
								try {
									if (checkRemoteExist()) {
										LOGGER.info(
										"Existing local repository found, pulling latest changes...");
										git = pullRepository();
									}else {
										LOGGER.error("Remote repository not found...");
									}
								} catch (final Exception e) {
									LOGGER.error(
											"Could not update existing local repository...",
											e);
								}
							}
						} catch (final GitAPIException e) {
							LOGGER.error("Unable to clone git repository at " + remoteUrl, e);
						}
						return null;
					}
				});
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	private boolean checkRemoteExist() {
        final LsRemoteCommand lsremoteCommand = Git.lsRemoteRepository();
        try {
            lsremoteCommand.setRemote(remoteUrl).setCredentialsProvider(user).call();
        } catch (GitAPIException e) {
            return false;
        }
        return true;
    }

    private Git pullRepository() throws GitAPIException, IOException {
        final Git git = Git.open(rootLocation.toFile());
        git.pull().setRebase(true)
                .setCredentialsProvider(user)
                .call();
        return git;
    }

    private Git cloneRepository() throws GitAPIException {
        final CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(rootLocation.toFile())
                .setCredentialsProvider(user);
        return cloneCommand.call();
    }

	private  <T> T synchronizedOperation(final Callable<T> callable) {
        try {
            if (directoryLock.tryLock(lockTimeoutSeconds, TimeUnit.SECONDS)) {
                try {
                    return callable.call();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    directoryLock.unlock();
                }
            } else {
                throw new RuntimeException(
                                "Attempt to acquire lock on working directory was timeout: "
                                 + lockTimeoutSeconds
                                 + "s. Maybe due to dead lock");
            }
        } catch (final InterruptedException e) {
            LOGGER.error("Thread interrupted. ", e);
        }
        return null;
    }
}
