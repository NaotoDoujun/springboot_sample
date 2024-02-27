import axios from 'axios';

jest.mock('axios');

const mockedData = [{
  "id": 1,
  "name": "Apple",
 }, {
  "id": 2,
  "name": "Banana",
 }];

describe('Mocking an API', () => {

  it('should fetch data from API', async () => {
    axios.get.mockResolvedValue({ data: mockedData });
  });

});