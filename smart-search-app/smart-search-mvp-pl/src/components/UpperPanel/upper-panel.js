import * as React from 'react';
import {useState} from "react";
import logotype from './logo.png';
import {Button, TextField} from "@mui/material";
import {fetchSearchData} from "../../api/api";
import './upper-panel.css'


const UpperPanel = (props) => {
    const [inputText, setInputText] = useState("");
    const {searchResult, setSearchResult } = props;
    const inputHandler = (e) => {
        //convert input text to lower case
        const lowerCase = e.target.value.toLowerCase();
        setInputText(lowerCase);
        console.log(lowerCase);
    };

    const fuzzySearchHandler = () => {
        setSearchResult({...searchResult, searchData:[]})
        const rqData = { count: 1000, from: 0, fieldName: "ALIAS_SUBSTR", query: inputText };
        fetchSearchData(searchResult, rqData, setSearchResult);
    };

    return <div className="main-container">
        <img src={logotype} className="search-logo" alt="logo" />
        <div className="search-input">
            <TextField
                id="outlined-basic"
                variant="outlined"
                fullWidth
                label="Умный поиск"
                onChange={inputHandler}
            />
        </div>
        <div className="flex-div">
            <Button color="primary" onClick={fuzzySearchHandler}>ПОИСК</Button>
        </div>
    </div>
};

export default UpperPanel;