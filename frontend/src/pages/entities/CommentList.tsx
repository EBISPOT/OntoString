
import { Box, Card, List, ListItem, Paper, Table, TableBody, TableCell, TableContainer, TableRow } from "@material-ui/core";
import React from "react";
import Provenance from "../../components/Provenance";
import Mapping from "../../dto/Mapping";
import Comment from "../../dto/Comment";

export default function CommentList(props:{mapping:Mapping}) {

    let { mapping } = props

    return <List>
        {
        mapping.comments.map((r:Comment) => {
            return <ListItem>
                <Paper>
                <Box mx={1} my={1}>
                <p>{r.body}</p>
                </Box>
                </Paper>
                <Box>
                <Provenance label="" provenance={r.created} />
                </Box>
            </ListItem>
        })
        }
    </List>
}


