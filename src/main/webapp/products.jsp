<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
ProductService ps = ProductServiceFactory.getInstance();
Collection<Product> plist = ps.getProducts(true);
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Product Maintain</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<style type="text/css">
body{
	/*text-align: center;*/
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

table{
	width: 50%;
}
body .divMain{
	overflow:auto;
    }
.divMain #floatDiv {
	position:absolute;
}
</style>
<script type="text/javascript">
$(function(){
	$(window).scroll(function () {
		var pos = $(document).scrollTop()+"px";
		$('#floatDiv').animate({top:pos},{duration:500,queue:false});
	});
	$('#backbtn').click(function(){
		//window.history.back();
		location.href='maintain.jsp';
	});
	$('#newProduct').click(function(){
		$( "#new-form" ).dialog('open');
	});
	
	$( "#new-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Add": function(){
				$.get( "newProduct.jsp", 
					   	{name:$('#name').val(),instanceName:$('#instanceName').val()}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Add new product failed:"+data);
					   		}
				  		},
				  		'text'
				);
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		open: function(event, ui) { 
		},
		close: function() {
		}
	});
	
	$( "#update-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Update": function(){
				$.get( "updateProduct.jsp", 
					   	{name:$('#name2').val(),isvalid:$('#isvalid').attr("checked"),instanceName:$('#instanceName2').val()}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Update product failed:"+data);
					   		}
				  		},
				  		'text'
				);
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		open: function(event, ui) { 
		},
		close: function() {
		}
	});
});
function updateProduct( name, valid, instanceName ){
	$( '#name2' ).val(name);
	if(valid=='true')
		$( '#isvalid' ).attr("checked",valid);
	$('#instanceName2').val(instanceName);
	$( "#update-form" ).dialog('open');
}
function delProduct(name){
	if(!window.confirm('Are you sure to delete this Product?'))
		return;
	$.get( "delProduct.jsp", 
		   	{name:name}, 
			function(data, textStatus){
		   		if($.trim(data)=='true'){
		   			location.reload();
		   		}else{
		   			alert("Delete product failed:"+data);
		   		}
	  		},
	  		'text'
	);
}
</script>
</head>
<body>
<div class="divMain">
	<div id="floatDiv">
		<input class="ui-widget" id="newProduct" type="button" value="Add Product" />
		<button id="backbtn" class="ui-widget">Back</button>
	</div>
	<br/>
		<table class="ui-widget">
		<thead class="ui-widget-header">
			<tr>
				<th>ProductName</th>
				<th>IsValid</th>
				<th>CreateTime</th>
				<th>InstanceName</th>
				<th>Operation</th>
			</tr>
		</thead>
			<%for( Product p : plist) {%>
			<tr>
				<td><%=p.getProductName()%></td>
				<td><%=(p.getInvalid()==Constants.PRODUCT_VALID)?"true":"false" %></td>
				<td><%=p.getCreateTime() %></td>
				<td><%=p.getInstanceName() %></td>
				<td>
					<input onclick="delProduct('<%=p.getProductName() %>')" type="button" value="Delete" />&nbsp;&nbsp;
					<input onclick="updateProduct('<%=p.getProductName() %>','<%=(p.getInvalid()==Constants.PRODUCT_VALID)?"true":"false" %>','<%=p.getInstanceName() %>')" type="button" value="Update" />
				</td>
			</tr>
			<%} %>
		<tfoot class="ui-widget-header">
			<tr>
				<th colspan="5"><hr/></th>
			</tr>
		</tfoot>
		</table>
	</div>
	
	<div id="new-form" title="New Product">
		<form>
			<fieldset>
				<label for="name">Product Name:</label>
				<input type="text" id="name" name="name" /><br/>
				<label for="instanceName">InstanceName:</label>
				<select id="instanceName">
					<%for(String instanceName: DbUtils.instances){ %>
					<option value="<%=instanceName%>"><%=instanceName%></option>
					<%} %>
				</select>
			</fieldset>
		</form>
	</div>
	
	<div id="update-form" title="Update Product">
		<form>
			<fieldset>
				<label for="name">Product Name:</label>
				<input type="text" id="name2" name="name2" readonly /><br/>
				<label for="isvalid">IsValid? :</label>
				<input type="checkbox" id="isvalid" name="isvalid"/><br/>
				<label for="instanceName2">InstanceName :</label>
				<select id="instanceName2">
					<%for(String instanceName: DbUtils.instances){ %>
					<option value="<%=instanceName%>"><%=instanceName%></option>
					<%} %>
				</select>
			</fieldset>
		</form>
	</div>
	
</body>
</html>