$(function() {
	// class='isStopped-button'が設定されたボタンをクリックしたらダイアログを表示する。
	$('.isStopped-button').on('click', function() {
		let result = confirm('変更しますか');

		if (result) {
			return true;
		} else {
			return false;
		}
	});
});
