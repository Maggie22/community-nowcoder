$(function(){
	$("#sendBtn").click(send_letter);
});

function send_letter() {
	var toUsername = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH + "/letter/add",
		{"toUsername":toUsername, "content":content},
		function (data){
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			if(data.code===0){
				$("#sendModal").modal("hide");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					window.location.reload();
				}, 1500);
			}
			else{
				setTimeout(function(){
					$("#hintModal").modal("hide");
				}, 1500);
			}
		}
	);


}

function delete_msg(obj) {
	// TODO 删除数据
	// $(obj).parents(".media").remove();
	// console.log($(obj).val());
	$.post(
		CONTEXT_PATH + "/letter/delete",
		{"id": $(obj).val()},
		function (data){
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			if(data.code === 0){
				$(obj).parents(".media").remove();
			}
			setTimeout(function(){
				$("#hintModal").modal("hide");
			}, 1500);
		}
	)
}

function close_toast(){
	$(this).parents(".media").remove();
}