
import { Checkbox, createStyles, lighten, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Theme, WithStyles, withStyles } from "@material-ui/core"
import React from "react"
import LoadingOverlay from "../../components/LoadingOverlay"
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
    saving:boolean

    onClickTerm:(term:OntologyTerm)=>void
}

interface State {
}

class MappingList extends React.Component<Props, State> {

    constructor(props:Props) {
        super(props)

        this.state = {
        }
    }

    render() {

        let { classes, project, entity, saving } = this.props

        return <LoadingOverlay active={saving}>
         <TableContainer component={Paper}>
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

          {entity.mappingSuggestions.map((suggestion:MappingSuggestion) => {

              let term = suggestion.ontologyTerm

              let selected = entity.mapping &&
                entity.mapping.ontologyTerms.filter(t => t.iri === term.iri).length > 0

              return <TableRow
                        selected={selected}
                        onClick={() => this.onClickTerm(suggestion.ontologyTerm)}
                        className={classes.tableRow + ' curator-suggested'}
                        key={project.name}>
                <TableCell>
                    <Checkbox checked={selected} />
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

          {entity.mapping &&
            entity.mapping.ontologyTerms.filter(t => 
                entity.mappingSuggestions.filter(s => s.ontologyTerm.iri === t.iri).length === 0
            )
            .map
            ((term:OntologyTerm) => {

              let selected = entity.mapping &&
                entity.mapping.ontologyTerms.filter(t => t.iri === term.iri).length > 0

              return <TableRow
                        selected={selected}
                        onClick={() => this.onClickTerm(term)}
                        className={classes.tableRow + ' curator-suggested'}
                        key={project.name}>
                <TableCell>
                    <Checkbox checked={selected} />
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
                    </LoadingOverlay>

    }


    onClickTerm = (term:OntologyTerm) => {

        this.props.onClickTerm(term)

    }

}

export default withStyles(styles)(MappingList)
