<form class="form-horizontal" action="request" method="post">
	<input type="hidden" name="action" value="importMarketData" />
	<fieldset>
		<legend>Input</legend>
		<div class="form-group">
			<label for="SECCode" class="col-lg-2 control-label">Security
				Code</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="SECCode" name="SECCode"
					placeholder="Security Code">
			</div>
		</div>
		<div class="form-group">
			<label for="StartDate" class="col-lg-2 control-label">Start
				Date</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="StartDate"
					name="StartDate" placeholder="Start Date (dd-MM-yyy)">
			</div>
		</div>
		<div class="form-group">
			<label for="EndDate" class="col-lg-2 control-label">End Date</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="EndDate" name="EndDate"
					placeholder="End Date (dd-MM-yyy)">
			</div>
		</div>
		<div class="form-group">
			<label for="dataSourceURL" class="col-lg-2 control-label">Source
				URL</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="dataSourceURL"
					name="dataSourceURL" placeholder="Data Source URL">
			</div>
		</div>
		<div class="form-group">
			<label for="targetCurrencyCode" class="col-lg-2 control-label">Target
				Currency Code</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="targetCurrencyCode"
					name="targetCurrencyCode" placeholder="Target Currency Code (Leave as blank if price conversion is not required)">
			</div>
		</div>
		<div class="form-group">
			<div class="col-lg-10 col-lg-offset-2">
				<button type="reset" class="btn btn-default">Cancel</button>
				<button type="submit" class="btn btn-primary">Submit</button>
			</div>
		</div>
	</fieldset>
</form>