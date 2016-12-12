<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <link rel="stylesheet" href="${ctxStatic}/tree/css/mailCss/noneStyle.css" type="text/css"/>
    <title>日程管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function js_method() {
            $("#mytable").append("")
        }

        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    if (CKEDITOR.instances.content.getData() == "") {
                        top.$.jBox.tip('请填写邮件内容', 'warning');
                    } else {
//                        loading('正在提交，请稍等...');
                        form.submit();
                    }
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });


        //存为草稿箱
        function deleteBy() {
            form1.action = '${ctx}/oa/mailInfo/saveDrafts';
            form1.submit();
        }
        //发送
        function send() {
            form1.action = '${ctx}/oa/mailInfo/send';
            form1.submit();
        }



        function getO(id)
        {
            if (typeof(id)=="string")
                return document.getElementById(id);
        }

        function appendAfterRow(tableID,RowIndex)
        {
//FUNCTION: 向指定行后面增加一行,列数和第一行的列数一样
            var o=document.getElementById(tableID);
            var refRow=RowIndex;
            var cells=o.rows[0].cells.length;
            if(refRow=="") refRow = getO("nRow").value;
            var v="";
            var newRefRow=o.insertRow(refRow);
            for (var i=0;i<cells;i++)
            {
                if(o.rows.length<10)
                    v="0"+o.rows.length+"0"+(i+1);
                else
                    v=o.rows.length+"0"+(i+1);
                newRefRow.insertCell(i).innerHTML=v;
            }
        }

    </script>
</head>
<body>
<div style="background-color: #ffffff">
    <table class="table">
        <tr class="tr1">
            <td colspan="2" style="padding-left: 15px">写信</td>
        </tr>
    </table>
    <table style="width: 98.5%;" id="mytable" id="tb01">
        <form:form  modelAttribute="mailInfo" action="" method="post"  id="form1"
                   class="form-horizontal">
            <tr>
                <td class="td1">收件人</td>
                <td class="td">
                    <form:select path="receiverId" class="input-medium" style="width:100%" itemValue="${mailInfo.receiverId}">
                        <form:option value="" label=""/>
                        <form:options items="${fns:getPhone()}" itemLabel="name" itemValue="id" htmlEscape="true" />
                    </form:select>
            </tr>
            <tr>
                <td class="td1">抄送人</td>
                <td class="td">
                    <form:select path="ccId" class="input-medium" style="width:100%" itemValue="${mailInfo.ccId}">
                        <form:option value="" label=""/>
                        <form:options items="${fns:getPhone()}" itemLabel="name" itemValue="id" htmlEscape="true" />
                    </form:select>
            </tr>
            <tr>
                <td class="td1">主题</td>
                <td class="td"><form:input path="theme" htmlEscape="true" type="text"
                                           style="width:99%" value="${mailInfo.theme}"></form:input></td>
            </tr>
            <tr>
                <td class="td1">附件</td>
                <td class="td" style="padding-bottom: 15px; height: 25px">
                    <form:hidden id="files" path="files" htmlEscape="false" maxlength="2000" class="input-xlarge"/>
                    <sys:ckfinder input="files" type="files" uploadPath="/oa/mailInfo" selectMultiple="true"/>
                </td>

            </tr>
            <tr style=" ">
                <td class="td1" valign="top">正文</td>
                <td style=" padding-left: 7px;">
                    <form:textarea id="content" htmlEscape="true" path="content" rows="3" maxlength="200"
                                   class="input-xxlarge" />
                    <sys:ckeditor replace="content" uploadPath="/oa/mailInfo" height="200px"/>
                </td>
            </tr>
            <tr style=" ">
                <td colspan="2" style="padding-left: 69px ;">发件人: <span>
                 ${fns:getUser().name}
                </span></td>
            </tr>
            <tr>
                <td colspan="2" style="padding-left: 69px ; padding-top: 7px">
                    <input type="submit" class="btn btn-primary" value="发送" onclick="send()">　
                    <input type="submit" class="btn btn-primary" value="存草稿" onclick="deleteBy()">
                </td>
            </tr>
        </form:form>
    </table>
</div>

</body>
</html>