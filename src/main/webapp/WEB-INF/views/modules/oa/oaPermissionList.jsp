<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>工作日志管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/oa/oaSummaryDay/day">评阅日总结</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="oaSummaryDay" action="${ctx}/oa/oaSummaryDay/loginId" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
            <li>    　　　　　<label>被评阅人：</label>
                <form:select path="loginId" class="input-medium">
                    <form:option value="" label=""/>    <%--htmlEscape="false"--%>
                    <form:options items="${fns:getAllPermission()}" itemLabel="name" itemValue="id" htmlEscape="true" value="${oaSummaryDay.loginId}" />
                </form:select>
            </li>
            <li><label>评阅日期：</label>
                    <input name="sumDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                           value="<fmt:formatDate value="${oaSummaryDay.sumDate}" pattern="yyyy-MM-dd"/>"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
            </li>
			<li class="btns"><input id="btnSeaSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>

    <form:form id="inputForm" modelAttribute="oaSummaryDay" action="${ctx}/oa/oaSummaryDay/saveEvel" method="post" class="form-horizontal">

        <div class="control-group" >
            <label class="control-label">任务完成：</label>
            <div class="controls">
                <table style="width:300px;">
                         <c:forEach items="${oaSummaryDay.oaScheduleList}" var="oaSchedule">
                             <tr>
                                 <td>
                                         ${fns:abbr(oaSchedule.content,50)}
                                 </td>
                             </tr>
                         </c:forEach>
                </table>
            </div>
        </div>
        <form:hidden path="id" value="${oaSummaryDay.id}"/>
        <sys:message content="${message}"/>

        <div class="control-group">
            <label class="control-label">工作总结：</label>
            <div class="controls">
                    <form:textarea path="content" readonly="true" htmlEscape="false" rows="4" maxlength="2000" class="input-xxlarge " value="${oaSummaryDay.content}"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">同事评阅：</label>
            <div class="controls">
                <form:textarea path="evaluate"  htmlEscape="false" rows="4" maxlength="2000" class="input-xxlarge " value="${oaSummaryDay.evaluate}"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
        <div class="form-actions">
            <shiro:hasPermission name="oa:oaSummaryDay:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        </div>
    </form:form>
</body>
</html>