import { Button, Paper, Link as LinkAlias } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Link } from "react-router-dom";

export default (<div className="flex flex-col flex-auto justify-center items-center">
  <Paper className="flex flex-col justify-center" variant="outlined" style={{ padding: "1em", marginBottom: "1em" }}>
    <div className="flex justify-center" style={{ paddingBottom: "1em" }}>
      {"404"}
    </div>
    <div>
      <Link to="/" className="no-underline" >
        <LinkAlias underline="hover" component="div">
          <Button variant="text" color="primary" style={{ textTransform: "none", marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}>
            <FormattedMessage id="ReturnToHomePage" defaultMessage="To home" />
          </Button>
        </LinkAlias>
      </Link>
    </div>
  </Paper>
</div>)