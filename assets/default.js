var TRANSITION_DURATION = 200;
var STATUS_DRIVING = 0;
var STATUS_STATION = 1;
var STATUS_PAUSE = 2;
var STATUS_STOP = 3;
var status = STATUS_DRIVING;
var page = 0;
var radio = 0;

$(window).resize(function() {
    // スマフォっぽい比率で固定
    $("body").css("font-size", $("body").width() / 7 + "%");
});
$(document).ready(function() {
    $(window).resize();
    start();
    setInterval(function() {
        $("#drive_screen_image").attr("src", "d" + page + ".png");
        $("#radio_image").attr("src", "radio" + radio + ".png");
        page = (page + 1) % 4;
        radio = (radio + 1) % 4;
    }, 5000);

    $("#inbound_reserve_list td").click(function(e) {
        $("#inbound_reserve_list td").not(this).parent().css("background-color", "transparent");
        $(this).parent().css("background-color", "lightblue");
        $("#inbound_reserve_button").css({"color": "black", "background-color": "tomato"});
    });

    $(".station-cell").click(function(e) {
        $(this).toggleClass("station-cell-selected");
        if ($(".station-cell").not(".station-cell-selected").length > 0) {
            return;
        }
        showCheckStartOverlay();
    });
});

function showDefaultScreen() {
    var selector = "";
    if (status == STATUS_DRIVING) {
        selector = "#drive_screen";
    } else if (status == STATUS_STATION) {
        selector = "#station_screen";
    }
    $(".screen").not(selector).hide(TRANSITION_DURATION);
    $(selector).show(TRANSITION_DURATION);
    $(".button_layout").css("background-color", "transparent");
}

function toggleScreen(targetId) {
    var result = true;
    var showId = targetId;
    if ($("#" + targetId).filter(":hidden").length == 0) {
        if (status == STATUS_DRIVING) {
            showId = "drive_screen";
        } else if (status == STATUS_STATION) {
            showId = "station_screen";
        }
        result = false;
    }
    $(".screen").not("#" + showId).hide(TRANSITION_DURATION);
    $("#" + showId).show(TRANSITION_DURATION);
    return result;
}

function showScheduleScreen() {
    if ($("#schedule_screen").filter(":hidden").length > 0) {
        toggleScheduleScreen();
    }
}

function toggleMapScreen() {
    var selector = "#map_button_layout";
    $(".button_layout").not(selector).css("background-color", "transparent");
    var bg = "";
    if (toggleScreen("map_screen")) {
        if (status == STATUS_DRIVING) {
            bg = "gsp.png";
        } else if (status == STATUS_STATION) {
            bg = "bsp.png";
        }
    } else {
        if (status == STATUS_DRIVING) {
            bg = "gs.png";
        } else if (status == STATUS_STATION) {
            bg = "bs.png";
        }
    }
    $("#map_button_bg").attr("src", bg);

    if (status == STATUS_DRIVING) {
        $("#schedule_button_bg").attr("src", "gs.png");
    } else if (status == STATUS_STATION) {
        $("#schedule_button_bg").attr("src", "bs.png");
    }
}

function toggleScheduleScreen() {
    var selector = "#schedule_button_layout";
    $(".button_layout").not(selector).css("background-color", "transparent");
    var bg = "";
    if (toggleScreen("schedule_screen")) {
        if (status == STATUS_DRIVING) {
            bg = "gsp.png";
        } else if (status == STATUS_STATION) {
            bg = "bsp.png";
        }
    } else {
        if (status == STATUS_DRIVING) {
            bg = "gs.png";
        } else if (status == STATUS_STATION) {
            bg = "bs.png";
        }
    }
    $("#schedule_button_bg").attr("src", bg);

    if (status == STATUS_DRIVING) {
        $("#map_button_bg").attr("src", "gs.png");
    } else if (status == STATUS_STATION) {
        $("#map_button_bg").attr("src", "bs.png");
    }
}

function showCheckStartOverlay() {
    $("#check_start_overlay").show(TRANSITION_DURATION);
}

function showCheckStopOverlay() {
    $("#check_stop_overlay").show(TRANSITION_DURATION);
}

function showMemoOverlay() {
    $("#memo_overlay").show(TRANSITION_DURATION);
}

function showOperatorMessageOverlay() {
    clearMessageNotify();
    $("#operator_message_overlay").show(TRANSITION_DURATION);
}

function showStopOverlay() {
    $("#stop_overlay").show(TRANSITION_DURATION);
}

function showPauseOverlay() {
    $("#pause_overlay").show(TRANSITION_DURATION);
}

function showAdminOverlay() {
    $("#admin_overlay").show(TRANSITION_DURATION);
}

function showInboundOverlay() {
    $("#inbound_overlay").show(TRANSITION_DURATION);
}

function start() {
    closeOverlay();
    status = STATUS_DRIVING;
    $(".frame").css("background-color", "lightgreen");
    $("#admin_button_bg").attr("src", "gs.png");
    $("#map_button_bg").attr("src", "gs.png");
    $("#schedule_button_bg").attr("src", "gb.png");
    $("#start_button_bg").attr("src", "gb.png");
    $("#goal_button_bg").attr("src", "gb.png");

    $("#start_button_layout").hide();
    $("#goal_button_layout").show();
    $("#status_text").text("走行中");
    showDefaultScreen();
    $(".station-cell").removeClass("station-cell-selected");
}

function goal() {
    status = STATUS_STATION;
    $(".frame").css("background-color", "lightblue");
    $("#admin_button_bg").attr("src", "bs.png");
    $("#map_button_bg").attr("src", "bs.png");
    $("#schedule_button_bg").attr("src", "bb.png");
    $("#start_button_bg").attr("src", "bb.png");
    $("#goal_button_bg").attr("src", "bb.png");

    $("#start_button_layout").show();
    $("#goal_button_layout").hide();
    $("#status_text").text("停車中");
    showDefaultScreen();
}

function closeOverlay() {
    $(".overlay").hide(TRANSITION_DURATION);
}

function up() {
    $("div.scroll").animate({scrollTop: $("div.scroll").scrollTop() - 300}, 'fast');
}

function down() {
    $("div.scroll").animate({scrollTop: $("div.scroll").scrollTop() + 300}, 'fast');
}

var notifyBlink = 0;

function startMessageNotify() {
    clearMessageNotify();
    var messageNotifyTimerId = 0;
    messageNotifyTimerId = setInterval(function() {
        $("#message_notify").toggle();
        notifyBlink++;
        if (notifyBlink < 6) {
            return;
        }
        $("#message_notify").hide();
        clearInterval(messageNotifyTimerId);
        notifyBlink = 0;
        $("#operator_message_overlay").show(TRANSITION_DURATION);
    }, 350);
}

function startScheduleChangedNotify() {
    clearMessageNotify();
    var messageNotifyTimerId = 0;
    messageNotifyTimerId = setInterval(function() {
        $("#message_notify").toggle();
        notifyBlink++;
        if (notifyBlink < 6) {
            return;
        }
        $("#message_notify").hide();
        clearInterval(messageNotifyTimerId);
        notifyBlink = 0;
        $("#schedule_changed_overlay").show(TRANSITION_DURATION);
    }, 350);
}

function clearMessageNotify() {
}

function showInboundSearchResult() {
    $("#inbound_search_result").show(TRANSITION_DURATION);
}

function hideInboundSearchResult() {
    $("#inbound_search_result").hide(TRANSITION_DURATION);
    $("#inbound_reserve_list td").parent().css("background-color", "transparent");
    $("#inbound_reserve_button").css({"color": "gray", "background-color": "lightgray"});
}
