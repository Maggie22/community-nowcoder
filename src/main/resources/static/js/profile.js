function follow(btn, targetUserId) {
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(
			CONTEXT_PATH + "/follow",
			{"targetUserId": targetUserId},
			function (data) {
				data = $.parseJSON(data);
				$("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					if(data.code === 0){
						$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
						if($("#followerNum").length>0){
							var oldNum = Number($("#followerNum").text());
							$("#followerNum").text(oldNum+1);
						}
					}
				}, 1500);
			}
		);

	} else {
		// 取消关注
		$.post(
			CONTEXT_PATH + "/unfollow",
			{"targetUserId": targetUserId},
			function (data) {
				data = $.parseJSON(data);
				$("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					if(data.code === 0){
						$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
						if($("#followerNum").length>0){
							var oldNum = Number($("#followerNum").text());
							$("#followerNum").text(oldNum-1);
						}
					}
				}, 1500);
			}
		);
	}
}