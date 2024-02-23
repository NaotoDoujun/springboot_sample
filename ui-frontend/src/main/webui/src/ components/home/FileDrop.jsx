import React, { useCallback, useMemo } from 'react';
import axios from 'axios';
import { useDropzone } from 'react-dropzone';
import { Box, Paper, Typography } from '@mui/material';
import { AppSettingsContext } from '../common';

const baseStyle = {
  flex: 1,
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  padding: 10,
  borderWidth: 2,
  borderRadius: 2,
  borderColor: '#eeeeee',
  borderStyle: 'dashed',
  backgroundColor: '#fafafa',
  color: '#bdbdbd',
  outline: 'none',
  transition: 'border .24s ease-in-out'
};

const focusedStyle = {
  borderColor: '#2196f3'
};

const acceptStyle = {
  borderColor: '#00e676'
};

const rejectStyle = {
  borderColor: '#ff1744'
};

function FileDrop() {
  const { setFiles } = React.useContext(AppSettingsContext);

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
      axios.get(`${window.API_BASE_URL}/api/git`)
      .then(res => {
        setFiles(res.data);
      });
    }).catch(error => {
      console.error(error);
    });
  }, [setFiles]);
  
  const { 
    getRootProps, 
    getInputProps, 
    isDragActive, 
    isFocused,
    isDragAccept,
    isDragReject } = useDropzone({ onDrop });

    const style = useMemo(() => ({
      ...baseStyle,
      ...(isFocused ? focusedStyle : {}),
      ...(isDragAccept ? acceptStyle : {}),
      ...(isDragReject ? rejectStyle : {})
    }), [
      isFocused,
      isDragAccept,
      isDragReject
    ]);
  
  return (
    <Box sx={{
      display: 'flex',
      flexWrap: 'wrap',
      '& > :not(style)': {
        mt: 1, p: 2,
      },
    }}>
      <Paper>
        <Typography variant="h5">File Drop</Typography>
        <div className="container">
          <div {...getRootProps({style})}>
            <input {...getInputProps()} />
            {
                isDragActive ?
                <p>Drop the files here ...</p> :
                <p>Drag 'n' drop some files here, or click to select files</p>
            }
          </div>
        </div>
      </Paper>
    </Box>
  );
};

export { FileDrop };