<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.npc.lottery.common.Constant"%>

<%@ taglib prefix="lottery" uri="/WEB-INF/tag/pagination.tld"%><!-- 分页标签 -->
<%@ taglib prefix="s" uri="/struts-tags"%>
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/css/admin/index.css" />
<script language="javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script language="javascript" src="${pageContext.request.contextPath}/js/jquery.form.js"></script>
<script language="javascript" src="../js/Forbid.js" type="text/javascript"></script>
<script type="text/javascript">
	$(document).ready(
			function() {

				// 文本框只能輸入小數 、整數
				$("#periodsNum").keyup(
						function() {
							var tmptxt = $(this).val();
							if ($(this).val() != "") {
								$(this).val(tmptxt.replace(/[^\d.]/g, ""));
								$(this).val($(this).val().replace(/^\./g, ""));
								$(this).val(
										$(this).val().replace(/\.{2,}/g, "."));
								$(this).val(
										$(this).val().replace(".", "$#$")
												.replace(/\./g, "").replace(
														"$#$", "."));
							}
						});

			});
</script>


<div class="content">
	<s:form id="sform" method="post" action="playOddsLogAction">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<!--控制表格头部开始-->
			  <td height="30" background="${pageContext.request.contextPath}/images/admin/tab_05.gif"><table width="100%" border="0" cellspacing="0" cellpadding="0">
			      <tr>
			        <td width="12" height="30"><img src="${pageContext.request.contextPath}/images/admin/tab_03.gif" width="12" height="30" /></td>
			        <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <tr>
			            <td width="21" align="left"><img src="${pageContext.request.contextPath}/images/admin/tb.gif" width="16" height="16" /></td>
			            <td width="124" align="left" class="F_bold">操盤日誌</td>
						<td width="179" align="center">&nbsp;</td>
			            <td class="t_right" width="1035">
			            	<table border="0" cellspacing="0" cellpadding="0">
							    <tr>
					                <td width="126">&nbsp;</td>
					                <td width="202">&nbsp;</td>
					                <td width="220" align="right">&nbsp;</td>
					                <td class="t_right">
			                				<select id="areaLottery" name="areaLottery"
												onchange="changeAreaLottery()">
													<option value="GDKLSF"
														<s:if test="#request.areaLottery.indexOf('GDKLSF')>-1">selected</s:if>>廣東快樂十分</option>
													<option value="CQSSC"
														<s:if test="#request.areaLottery.indexOf('CQSSC')>-1">selected</s:if>>重慶時時彩</option>
													<option value="BJ"
														<s:if test="#request.areaLottery.indexOf('BJ')>-1">selected</s:if>>北京賽車(PK10)</option>
													<option value="K3"
														<s:if test="#request.areaLottery.indexOf('K3')>-1">selected</s:if>>江苏骰寶(快3)</option>
													<option value="NC"
														<s:if test="#request.areaLottery.indexOf('NC')>-1">selected</s:if>>幸运农场</option>
											</select>
									</td>
					                <td width="156" class="t_right">
					                	<s:select id="periodsNum" name="periodsNum" onchange="$('#sform').submit();" style="width:135px"
													list="#request.periodsNumList" listKey="key" listValue="value"></s:select>	
									    <input name="periodsNumType" type="hidden" value="<s:property value="#request.periodType"/>"/>
									</td>
									<td width="186" class="t_right">
								       	分類：
										<s:select id="playType" name="playType" onchange="$('#sform').submit();"
											 style="width:115px" list="#request.playTypeMap" listKey="key"
											listValue="value" headerKey="" headerValue="--- ALL ---"></s:select>
									</td>
				              </tr>
			               </table>
						</td>
			            </tr></table></td>
			        <td width="16"><img src="${pageContext.request.contextPath}/images/admin/tab_07.gif" width="16" height="30" /></td>
			      </tr>
			    </table></td>
			<!--控制表格头部结束-->
			 <tr>
			    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
			      <tr>
			        <td width="8" background="${pageContext.request.contextPath}/images/admin/tab_12.gif">&nbsp;</td>
			        <td align="center" valign="top">
					<!-- 表格内容开始 一行六列-->
					<!-- 表格内容结束 一行六列-->
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="king mt4">
  
				<tbody>
					<tr>
						<th width="10%">類型</th>
						<th width="10%">操盤明細</th>
						<th width="30%">操盤手</th>
						<th width="30%">操盤時間</th>
						<th width="10%">IP</th>
						<th width="10%">IP歸屬</th>
					</tr>

					<% int count = 1; %>
					<s:iterator value="#request.page.result" status="branch">
						<tr onMouseOver="this.style.backgroundColor='#FFFFA2'"
							onmouseout="this.style.backgroundColor=''">
							<td><s:property value="oddsTypeNameLog" escape="false" /></td>
							<td><s:property value="playType.finalTypeName" escape="false" /> @ <span class="red"><s:property value="realOddsNew" escape="false" /></span></td>
							<td><s:property value="realUpdateUserNewAccount.account" escape="false" /></td>
							<td><s:date name="realUpdateDateNew" format="yyyy-MM-dd HH:mm:ss" /></td>
							<s:if test="type==1">
								<td colspan="2" >
									 額度到賠率自動降
								</td>
								</s:if>
								<s:else>
									<td><s:property value="ip" escape="false" /></td>
								<td>
									<s:property value="ipBelongTo" escape="false" />
								</td>
								</s:else>
						</tr>
					</s:iterator>
				</tbody>
			</table></td>
	        <td width="8" background="${pageContext.request.contextPath}/images/admin/tab_15.gif">&nbsp;</td>
	      </tr>
	    </table></td>
	  </tr>
		<tr>
		    <td height="35" background="${pageContext.request.contextPath}/images/admin/tab_19.gif"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		      <tr>
		        <td width="12" height="35"><img src="${pageContext.request.contextPath}/images/admin/tab_18.gif" width="12" height="35" /></td>
		        <td align="center">
		         <lottery:paginate url="${pageContext.request.contextPath}/admin/playOddsLogAction.action" param="periodsNum=${periodsNum}&areaLottery=${areaLottery}" />
		        
		        </td>
			   <td width="16"><img src="${pageContext.request.contextPath}/images/admin/tab_20.gif" width="16" height="35" /></td>
		      </tr>
		    </table></td>
		  </tr>
<!--控制底部操作按钮结束-->
     </table>
		<!--中间内容结束-->
	</s:form>
	<script type="text/javascript">
		function changeAreaLottery()
		{
			$("#playType").val("");
			$("#periodsNum").attr("value",'');
			$('#sform').submit();
		}
	</script>
</div>
</html>
