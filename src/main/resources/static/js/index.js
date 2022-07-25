$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 发送异步请求
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	var code;
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		function (data){
			// 在提示框中显示信息
			data = $.parseJSON(data);
			console.log(data);
			code = data.code;
			$("#hintBody").text(data.msg);

			if (code === 0){
				// 隐藏发布框
				$("#publishModal").modal("hide");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					window.location.reload();
				}, 1500);
				// 刷新页面

			}

			// 提示框
			$("#hintModal").modal("show");
		}
	);

}