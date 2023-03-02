
export default function Footer() {
  return (
    <footer className="footer">
      <span className="footer-legal">
        <i className="icon icon-common icon-copyright icon-spacer" />
        EMBL-EBI&nbsp;2023
      </span>
      <a href={process.env.REACT_APP_EBI_LICENSING} className="footer-link">
        Licensing
      </a>
    </footer>
  );
}
