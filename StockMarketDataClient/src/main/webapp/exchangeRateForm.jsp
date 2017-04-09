<form class="form-horizontal" action="request" method="post">
	<input type="hidden" name="action" value="exchangeRate" />
	<fieldset>
		<legend>Input</legend>
		<div class="form-group">
			<label for="baseCurrencyCode" class="col-lg-2 control-label">Base
				Currency Code</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" id="baseCurrencyCode" name="baseCurrencyCode"
					placeholder="Base Currency Code">
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