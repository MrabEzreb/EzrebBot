function gotohome() {
    window.location = "index.html";
}
window.onload = function() {
    document.getElementsByClassName("title")[0] .onclick = gotohome;
}