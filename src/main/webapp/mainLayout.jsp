<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<html>
<head>
<title><tiles:getAsString name="title"/></title>
</head>
<body>

<tiles:insert attribute="header"/>

<tiles:insert attribute="body"/>

<tiles:insert attribute="footer"/>

</body>
</html>