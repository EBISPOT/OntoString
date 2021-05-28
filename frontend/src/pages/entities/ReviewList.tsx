
import { Box, Card, List, ListItem, Paper, Table, TableBody, TableCell, TableContainer, TableRow } from "@material-ui/core";
import React from "react";
import Provenance from "../../components/Provenance";
import Mapping from "../../dto/Mapping";
import Review from "../../dto/Review";

export default function ReviewList(props:{mapping:Mapping}) {

    let { mapping } = props

    return <List>
        {
        mapping.reviews.map((r:Review) => {
            return <ListItem>
                <Paper>
                <Box mx={1} my={1}>
                <p>{r.comment}</p>
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


