
import { Box, Card, List, ListItem, Paper, Table, TableBody, TableCell, TableContainer, TableRow } from "@material-ui/core";
import React from "react";
import AuditEntry from "../dto/AuditEntry";
import { EntityStatus } from "../dto/Entity";
import formatDate from "../formatDate";
import StatusBox from "./StatusBox";

let actionToText = new Map<string,string>();
actionToText.set('ADDED_MAPPING', 'added a mapping')
actionToText.set('ADDED_SUGGESTION', 'added a suggestion')
actionToText.set('REMOVED_SUGGESTION', 'removed a suggestion')
actionToText.set('UPDATED_MAPPING', 'updated the mapping')

export default function AuditTrail(props:{trail:AuditEntry[]}) {

    let { trail } = props

    return <List>
        {
        trail.map((e:AuditEntry) => {
            return <ListItem>
                <Box m={1}>
                <Box m={1}>
                <b>{e.user.name}</b> {actionToText.get(e.action) || e.action} {formatDate(e.timestamp)}
                </Box>
                {
                     e.metadata.length > 0 &&
                        <TableContainer component={Paper}>
                        <Table size='small'>
                            <TableBody>
                            {
                        e.metadata.map(m => {
                            return <TableRow><TableCell align="left">{m.key}</TableCell><TableCell align="left">{m.value}</TableCell></TableRow>
                        })
                        }
                            </TableBody>
                        </Table>
                        </TableContainer>
                    }
                    </Box>
            </ListItem>
        })
        }
    </List>
}


