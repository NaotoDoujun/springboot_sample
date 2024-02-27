import React from 'react';
import { render, screen } from '@testing-library/react';
import { FileDrop } from '../home/FileDrop';
import { Commit } from '../home/Commit';
import { Home } from '../home/Home';

describe('check title', () => {  
    it('title', () => {  
        render(<Home/>);
        expect(screen.getByText('UI-Frontend Sample')).toBeInTheDocument();
    });

    it('called filedrop component', () => {  
        render(<Home/>);
        expect(FileDrop).toHaveBeenCalled();
    });

});

jest.mock('../home/FileDrop');

describe('check filedrop component', () => {  
    it('called filedrop component', () => {  
        render(<Home/>);
        expect(FileDrop).toHaveBeenCalled();
    });
});

jest.mock('../home/Commit');

describe('text commit component', () => {  
    it('called commit component', () => {  
        render(<Home/>);
        expect(Commit).toHaveBeenCalled();
    });
});