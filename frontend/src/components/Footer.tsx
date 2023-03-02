import { Copyright } from "@material-ui/icons";

export default function Footer() {
  return (
    <footer className="footer">
      <span className="footer-legal">
        <i className="icon icon-common icon-copyright icon-spacer" />
        EMBL-EBI&nbsp;2023
      </span>
      <a href="https://www.ebi.ac.uk/licencing" className="footer-link">
        Licensing
      </a>
    </footer>
  );
}
