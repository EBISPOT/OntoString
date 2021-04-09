
import { Checkbox, createStyles, lighten, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Theme, WithStyles, withStyles } from "@material-ui/core"
import React from "react"
import TermStatusBox from "../../components/TermStatusBox"
import Entity from "../../dto/Entity"
import Mapping, { CreateMapping } from "../../dto/Mapping"
import MappingSuggestion from "../../dto/MappingSuggestion"
import OntologyTerm from "../../dto/OntologyTerm"
import Project from "../../dto/Project"

const styles = (theme:Theme) => createStyles({
    tableRow: {
        "&": {
            cursor: 'pointer'
        },
        "&:hover": {
            backgroundColor: lighten(theme.palette.primary.light, 0.85)
        }
    }
})

interface Props extends WithStyles<typeof styles> {
    project:Project
    entity:Entity

    onRemoveMappingTerm:(term:OntologyTerm)=>void
}

interface State {
}

class MappingTermList extends React.Component<Props, State> {

    constructor(props:Props) {
        super(props)

        this.state = {
        }
    }

    render() {

        let { classes, project, entity } = this.props

        return <TableContainer component={Paper}>
        <Table aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell></TableCell>
              <TableCell align="left">Term</TableCell>
              <TableCell align="left">Term Label</TableCell>
              <TableCell align="left">Term Status</TableCell>
              {/* <TableCell align="left">Suggested By</TableCell>
              <TableCell align="left">Term Status</TableCell> */}
            </TableRow>
          </TableHead>
          <TableBody>

          {entity.mapping && entity.mapping.ontologyTerms.map((term:OntologyTerm) => {

              return <TableRow
                        selected={true}
                        onClick={() => this.props.onRemoveMappingTerm(term)}
                        className={classes.tableRow}
                        key={project.name}>
                <TableCell>
                    x
                </TableCell>
                <TableCell align="left">
                    {term.curie}
                </TableCell>
                <TableCell align="left">
                    {term.label}
                </TableCell>
                <TableCell align="left">
                    <TermStatusBox status={term.status} />
                </TableCell>
              </TableRow>
            })}

            {/* <TableRow>
                <TableCell colSpan={4} align="right">
                    <CreateProjectDialog onCreate={this.onCreateProject} />
                </TableCell>
            </TableRow> */}
          </TableBody>
        </Table>
      </TableContainer>

    }


    onClickSuggestion = (term:OntologyTerm) => {

        this.props.onRemoveMappingTerm(term)

    }

}

export default withStyles(styles)(MappingTermList)
