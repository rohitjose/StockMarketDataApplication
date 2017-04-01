<form class="form-horizontal" action="request" method="post">
	<input type="hidden" name="action" value="importMarketData" />
	<fieldset>
		<legend>Input</legend>
		<div class="form-group">
			<label for="SECCode" class="col-lg-2 control-label">Security
				Code</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="SECCode"
					placeholder="Security Code">
			</div>
		</div>
		<div class="form-group">
			<label for="StartDate" class="col-lg-2 control-label">Start
				Date</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="StartDate"
					placeholder="Start Date">
			</div>
		</div>
		<div class="form-group">
			<label for="EndDate" class="col-lg-2 control-label">End Date</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="EndDate"
					placeholder="End Date">
			</div>
		</div>
		<div class="form-group">
			<label for="dataSourceURL" class="col-lg-2 control-label">Source
				URL</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="dataSourceURL"
					placeholder="Data Source URL">
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