<%@ page import="java.util.ArrayList,java.util.Arrays"%>
<div class="panel-group" id="panel-343418">
	<%
		String errorIndicator = (String) request.getAttribute("responseStatus");
		String responseContent = (String) request.getAttribute("serviceResponseContents");
		ArrayList<String> eventSetIds = (ArrayList<String>) session.getAttribute("eventSetIds");

		if (errorIndicator == "success") {
	%>
	<div class="panel panel-success">
		<%
			} else if (errorIndicator == "error") {
		%>
		<div class="panel panel-danger">
			<%
				} else {
			%>
			<div class="panel panel-info">
				<%
					}
				%>
				<div class="panel-heading">
					<a class="panel-title" data-toggle="collapse"
						data-parent="#panel-343418" href="#panel-element-A1">Composed
						Service Run Response</a>
				</div>
				<div id="panel-element-A1" class="panel-collapse collapse in">
					<%
						if (responseContent != null) {
					%>
					<div class="panel-body">
						<!-- HTML table display begin -->
						<%
							if (errorIndicator == "success") {
									ArrayList<String> recordLines = new ArrayList<String>(Arrays.asList(responseContent.split("\n")));
						%>
						<table class="table table-striped">
							<tr>
								<!-- table header -->
								<%
									for (String head : recordLines.get(0).split(",")) {
								%>
								<td><%=head%></td>
								<%
									}
								%>
							</tr>
							<%
								recordLines.remove(0);
										for (String line : recordLines) {
							%>
							<tr>
								<%
									for (String item : line.split(",", -1)) {
								%>
								<td><%=item%></td>
								<%
									}
								%>
							</tr>
							<%
								}
							%>

						</table>
						<%
							} else if (errorIndicator == "error") {
						%>
						<div class="panel-body"><%=responseContent%></div>
						<%
							}
						%>
						<!-- HTML table display end -->
					</div>

					<%
						} else {
					%>
					<div class="panel-body"></div>
					<%
						}
					%>
				</div>
			</div>
			<div class="panel panel-info">
				<div class="panel-heading">
					<a class="panel-title collapsed" data-toggle="collapse"
						data-parent="#panel-343418" href="#panel-element-B1">List of
						Your Current Market Data (Event Set IDs)</a>
				</div>
				<div id="panel-element-B1" class="panel-collapse collapse">
					<div class="panel-body">
						<%
							if (eventSetIds == null) {
						%>
						No Event Sets Requested
						<%
							} else if (eventSetIds != null) {
						%>
						<ul>
							<%
								for (String eventSetID : eventSetIds) {
							%>
							<li><%=eventSetID%></li>
							<%
								}
								}
							%>
						</ul>
					</div>
				</div>
			</div>
			<!-- Only on downloadFile() call -->
			<%
				String sourceFile = (String) request.getAttribute("sourceFileData");

				if (sourceFile != null) {
			%>
			<div class="panel panel-success">
				<div class="panel-heading">
					<a class="panel-title" data-toggle="collapse"
						data-parent="#panel-343418" href="#panel-element-8178ZX">Requested
						Events Set File</a>
				</div>
				<div id="panel-element-8178ZX" class="panel-collapse collapse in">
					<div class="panel-body">
						<textarea rows="20" cols="50"><%=sourceFile%></textarea>
					</div>
				</div>
			</div>
			<%
				}
			%>
			<!-- End of Download file display -->
		</div>