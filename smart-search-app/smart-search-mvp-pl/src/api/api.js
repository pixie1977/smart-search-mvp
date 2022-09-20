import axios from "axios";

const PREFIX = "http://localhost:8081/"

const SEARCH_URL = PREFIX+"smart-search/search"

const headers = {
    'Acccess-Allow-Credential': 'origin',
    'Access-Control-Allow-Origin': '*',
    'Content-Type': 'Application/json',
    'Access-Control-Allow-Headers': 'Origin, X-Requested-With, privatekey, Content-Type, Accept',
}

const params = {
    mode: 'no-cors',
    headers: headers,
    origin: "*",
    methods: "GET,PUT,POST,DELETE, OPTIONS",
    withCredentials: false,
    credentials: 'same-origin',
}

export const fetchSearchData = async (data, rqData, setDataHandler) => {
    try {
        setDataHandler({...data, isFetching: true})
        const response = await axios.post(SEARCH_URL, rqData, params);
        setDataHandler({...data, searchData: response.data.items, isFetching: false});
    } catch (e) {
        console.log(e);
        setDataHandler({...data, isFetching: false});
    }
};