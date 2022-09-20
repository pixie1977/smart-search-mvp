import * as React from 'react';
import {makeStyles} from "@material-ui/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";

const useStyles = makeStyles({
    table: {
        maxWidth: '100vh',
        overFlow: 'scroll',
        maxHeight: '100vh'
    },
    tableContainer: {
        maxHeight: '90vh'
    }
});

function DataList(props) {
    const { searchResult } = props;
    const classes = useStyles();
    console.log(searchResult)
    let i=0;
    return (

    <Paper>
        <TableContainer className={classes.tableContainer} >
            <Table className={classes.table} aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell align="right">ЛОГИН</TableCell>
                        <TableCell align="right">ИМЯ</TableCell>
                        <TableCell align="right">ФАМИЛИЯ</TableCell>
                        <TableCell align="right">Возраст</TableCell>
                        <TableCell align="right">Город</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {searchResult.searchData.map((row) => (
                        <TableRow key={i++}>
                            <TableCell align="right">{row.login}</TableCell>
                            <TableCell align="right">{row.firstName}</TableCell>
                            <TableCell align="right">{row.lastName}</TableCell>
                            <TableCell align="right">{row.city}</TableCell>
                            <TableCell align="right">{row.age}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    </Paper>
    )
}

export default DataList
