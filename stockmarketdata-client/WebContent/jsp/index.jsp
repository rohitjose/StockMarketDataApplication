<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<!-- IE Edge Meta Tag -->
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- Viewport -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- Bootstrap CSS -->

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-switch/3.3.2/js/bootstrap-switch.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<title>StockMarketDataClient</title>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="jumbotron" style="background-color: white;">
					<h1 class="page-header">Market Data Services</h1>
					<p>A portal for all your stock market data needs!</p>
					<div class="row">
						<div class="col-md-6">
							<%@ include file="requestPane.jsp"%>
						</div>
						<div class="col-md-6">
							<%@ include file="responsePane.jsp"%>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>