import React, { useCallback } from 'react';
import axios from 'axios';
import { useDropzone } from 'react-dropzone';

function FileDrop() {

    const onDrop = useCallback(acceptedFiles => {
        const formData = new FormData();
        acceptedFiles.forEach(file => {
            formData.append('files', file);
        });
        axios.post(`${window.API_BASE_URL}/api/git`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(response => {
            console.log(response.data);
          }).catch(error => {
            console.error(error);
          });
    }, []);
    
    const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });
    
    return (
        <div {...getRootProps()}>
        <input {...getInputProps()} />
        {
            isDragActive ?
            <p>Drop the files here ...</p> :
            <p>Drag 'n' drop some files here, or click to select files</p>
        }
        </div>
    );
}

export { FileDrop };