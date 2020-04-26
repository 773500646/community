$(function() {
    // 展开二级评论
    $(".collapseComments").click(function(){
        var _self = $(this);
        var id = $(this).parents(".media-body").data("id");
        var comments = $("#comment-" + id);
        if(comments.hasClass("in")) {
            // 折叠
            $(this).removeClass("c-blue");
            comments.removeClass("in");
        } else {
            // 展开
            $(this).addClass("c-blue");
            comments.addClass("in");
            $.getJSON("/comment/" + id, function(data) {
                var str = '';
                if (data.code == 200 ) {
                    $.each(data.data,function (index, value) {
                      str += '<div class="comments">\n' +
                          '          <div class="media-left">\n' +
                          '              <a href="#">\n' +
                          '                  <img class="media-object img-rounded" alt="" src="'+ value.user.avatarUrl +'">\n' +
                          '              </a>\n' +
                          '          </div>\n' +
                          '          <div class="media-body">\n' +
                          '              <h5 class="media-heading">\n' +
                          '                  <span>'+ value.user.name +'</span>\n' +
                          '              </h5>\n' +
                          '              <div class="mt-10">' + value.content + '</div>\n' +
                          '              <div>\n' +
                          '                  <span class="c-gray fr">'+ moment(value.gmtCreate).format("YYYY-MM-DD") +'</span>\n' +
                          '              </div>\n' +
                          '          </div>\n' +
                          '          <hr>\n' +
                          '      </div>'
                    })
                    _self.parents(".media-body").find(".collapse-box .comments").remove();
                    _self.parents(".media-body").find(".collapse-box .collapse-box-item").before(str);
                    //_self.parents(".media-body").find(".collapse-box").appendTo(str);
                }
            })
        }
    })

    // 提交回去
    $("#question_Reply").click(function(){
        var question_id = $("#question_id").val();
        var question_textarea = $("#question_textarea").val();
        comment2target(question_id, 1, question_textarea);
    })

    $(".collapse-box-item .btn-success").click(function () {
        var id = $(this).parents(".media-body").data("id");
        var content = $(this).siblings("input").val();
        comment2target(id, 2, content);
    })

    function comment2target(targetId, type, content) {
        if(!content) {
            return
        }
        $.ajax({
            type: "post",
            url: "/comment",
            contentType: "application/json",
            data: JSON.stringify({
                "parentId": targetId,
                "content": content,
                "type": type
            }),
            success: function (response) {
                if (response.code == 200) {
                    //$("#comment_section").hide();
                    window.location.reload();
                } else {
                    if (response.code == 2003) {
                        var isAccepted = confirm(response.message);
                        if(isAccepted) {
                            window.open("https://github.com/login/oauth/authorize?client_id=c286da103f3cfd3ef90c&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                            window.localStorage.setItem("close_page", true);
                        }
                    } else {
                        alert(response.message)
                    }
                }
                console.log(response);
            },
            dataType: "json"
        })
    }

    $("#tag").click(function(){
        $(".tag-content-box").show();
    })
})


function selectTag(value) {
    var previous = $("#tag").val();
    if (previous.indexOf(value) == -1) {
        if (previous) {
           $("#tag").val(previous + "," + value);
        } else {
            $("#tag").val(value);
        }
    }
}