<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="/">
		<html>
			<xsl:apply-templates/>
		</html>
	</xsl:template>
	<xsl:template match="purchaseOrder">
		<head>
			<xsl:call-template name="poHeader"/>
		</head>
		<body>
			<xsl:call-template name="poBody"/>
		</body>
	</xsl:template>
	<xsl:template name="poHeader">
		<title>Purchase Order
			<xsl:value-of select="@orderNumber"/>
		</title>
	</xsl:template>
	<xsl:template name="poBody">
		<h1>Purchase Order</h1>
		<table>
			<tr>
				<td valign="top">
					<xsl:call-template name="poData"/>
				</td>
				<td valign="top">
					<xsl:apply-templates select="shipTo"/>
				</td>
				<td valign="top">
					<xsl:apply-templates select="billTo"/>
				</td>
			</tr>
		</table>
		<xsl:apply-templates select="items"/>
	</xsl:template>
	<xsl:template name="poData">
		<xsl:comment>purchase order data</xsl:comment>
		<table>
			<tr>
				<th>Order Number:</th>
				<td>
					<xsl:value-of select="@orderNumber"/>
				</td>
			</tr>
			<tr>
				<th>Order Date:</th>
				<td>
					<xsl:value-of select="@orderDate"/>
				</td>
			</tr>
			<tr>
				<th>Comments:</th>
				<td>
					<xsl:apply-templates select="comment" mode="po"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="shipTo">
		<xsl:comment>shipping information</xsl:comment>
		<div>
			<b>Ship To:</b>
		</div>
		<xsl:call-template name="address"/>
	</xsl:template>
	<xsl:template match="billTo">
		<xsl:comment>billing information</xsl:comment>
		<div>
			<b>Bill To:</b>
		</div>
		<xsl:call-template name="address"/>
	</xsl:template>
	<xsl:template name="address">
		<xsl:comment>billing information</xsl:comment>
		<div>
			<xsl:value-of select="name"/>
		</div>
		<div>
			<xsl:value-of select="street"/>
		</div>
		<div>
			<xsl:value-of select="city"/>
			<xsl:text>, </xsl:text>
			<xsl:value-of select="state"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="zip"/>
		</div>
		<div>
			<xsl:value-of select="@country"/>
		</div>
	</xsl:template>
	<xsl:template match="items">
		<xsl:comment>order items</xsl:comment>
		<table border="1">
			<tr>
				<th>Pos.</th>
				<th>Part No.</th>
				<th>Product</th>
				<th>Image</th>
				<th>Qty.</th>
				<th>Price</th>
				<th>Ship Date</th>
			</tr>
			<xsl:apply-templates select="item"/>
		</table>
	</xsl:template>
	<xsl:template match="item">
		<xsl:comment>item number
			<xsl:value-of select="position()"/>:
			<xsl:value-of select="product/productName"/>
		</xsl:comment>
		<tr>
			<td>
				<xsl:value-of select="position()"/>
			</td>
			<td>
				<xsl:value-of select="product/@partNum"/>
			</td>
			<td>
				<xsl:value-of select="product/productName"/>
			</td>
			<td>
				<xsl:if test="product/productImage">
					<img src="@product/productImage/url" />
					<br/>
					<xsl:value-of select="productImage/text"/>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="quantity"/>
			</td>
			<td>
				<xsl:value-of select="concat('$', USPrice)"/>
			</td>
			<td>
				<xsl:value-of select="shipDate"/>
			</td>
		</tr>
		<xsl:apply-templates select="comment" mode="item"/>
	</xsl:template>
	<xsl:template match="comment" mode="po">
		<div>
			<!-- BAD EXAMPLE: this should trigger Rule E.2 because it copies the entire
			     subtree without further filtering -->
			<xsl:copy-of select="."/>
		</div>
	</xsl:template>
	<xsl:template match="comment" mode="item">
		<tr>
			<td/>
			<td colspan="5">
				<b>Comment:</b>
				<xsl:text> </xsl:text>
				<!-- BAD EXAMPLE: this should trigger Rule E.2 because it copies the entire
				     subtree without further filtering -->
				<xsl:copy-of select="."/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>