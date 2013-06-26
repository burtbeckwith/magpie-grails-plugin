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
</table>


<script type="text/javascript">

    $(function(){ prepareConsole() });

</script>


</body>
</html>