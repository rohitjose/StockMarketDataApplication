<form class="form-horizontal" action="request" method="post">
	<input type="hidden" name="action" value="convertMarketData" />
	<fieldset>
		<legend>Input</legend>
		<div class="form-group">
			<label for="eventSetId" class="col-lg-2 control-label">Event
				Set ID</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="eventSetId" name="eventSetId"
					placeholder="Event Set ID">
			</div>
		</div>
		<div class="form-group">
			<label for="targetCurrencyCode" class="col-lg-2 control-label">Target
				Currency Code</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="targetCurrencyCode" name="targetCurrencyCode"
					placeholder="Target Currency Code">
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