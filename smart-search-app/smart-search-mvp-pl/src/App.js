import * as React from 'react';
import {useState} from "react";
import './App.css';
import UpperPanel from "./components/UpperPanel/upper-panel";
import DataList from "./components/DataList/data-list";

function App() {
    const [searchResult, setSearchResult] = useState({searchData: [], isFetching: false});
    return (
        <div>
            <header className="App-header">
                <UpperPanel searchResult={searchResult} setSearchResult={setSearchResult}/>
                <DataList searchResult={searchResult}/>
            </header>
        </div>
    );
}

export default App;
