<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>App Report Card ~ Restful Demo</title>

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



        <tr id="errandsDisplay">
            <td>

                <table id="errands" width="100%" cellpadding='4' cellspacing='0' border='1'>

                    <tr bgcolor='lightyellow'>

                        <td width="30%">Name</td>
                        <td width="10%">Url</td>
                        <td width="20%">Cron</td>
                        <td width="20%">Enforced Content Type</td>
                        <td width="10%">Active</td>

                    </tr>


                </table>

            </td>
        </tr>

</table>


<script type="text/javascript">

    $(function(){ prepareConsole() });

</script>


</body>
</html>