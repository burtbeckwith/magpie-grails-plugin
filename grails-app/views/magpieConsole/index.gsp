<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Magpie Console</title>

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'magpie.css')}" />

    <g:javascript src="magpie.js"/>
    <g:javascript library="jquery" plugin="jquery"/>


</head>
<body>


<table align="center" width='1000'>
    <tr>
        <td align='left'>
            Magpie Console
            <hr size=1/>
        </td>
    </tr>


    <tr>
      <td>

          <div id="proposedErrandFeedback"></div>
          <br/>

          <form>
              <table width="100%" cellpadding='4' cellspacing='0' border='1'>
              <tr>
                  <td valign='top' bgcolor="#ffb6c1">
                    Name:
                  </td>
                  <td valign='top' >
                    <g:textField id='proposedErrandName' name='name'/>
                    <div id="proposedErrandNameFeedback"></div>
                  </td>
                  <td valign='top' bgcolor="#ffb6c1">
                      Url:
                  </td>
                  <td valign='top' >
                      <g:textField id='proposedErrandUrl' name='url'/>
                      <div id="proposedErrandUrlFeedback"></div>
                   </td>
              </tr>
              <tr>
                  <td valign='top' bgcolor="#ffb6c1">
                      Cron:
                  </td>
                  <td valign='top' >
                      <g:textField id='proposedErrandCronExpression' name='cronExpression'/>
                      <div id="proposedErrandCronExpressionFeedback"></div>
                  </td>

                  <td valign='top' bgcolor="#add8e6">
                      Content-type:
                  </td>
                  <td valign='top' >
                      <g:textField id='proposedErrandEnforcedContentTypeForRendering' name='enforcedContentTypeForRendering'/>
                  </td>
              </tr>
              <tr>
                  <td colspan=4 align='right'>
                      &nbsp;
                      <input id="createNewErrandSubmitButton" type='submit' value="Create New Errand"/>
                  </td>

              </tr>

              </table>
          </form>

      </td>
    </tr>



        <tr id="errandsDisplay">
            <td>

                <table id="errands" width="100%" cellpadding='4' cellspacing='0' border='1'>

                    <tbody id="errandRows">

                    </tbody>
                </table>

            </td>
        </tr>

</table>


<script type="text/javascript">

    $(function(){ prepareConsole() });

</script>


</body>
</html>