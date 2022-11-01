# Hitomi.la Downloader

Hitomi 이미지를 다운로드 해주는 JAVA 라이브러리 입니다.

클래스:<br>
Hitomi_Download(String gallery_number)<br>
get_gallery_info(String gallery_number)<br>

gallery_number: 작품의 주소에 있는 번호를 뜻합니다.<br>
흔히 말하는 품번입니다.

<br>
함수:<br>
void Hitomi_Download.download_gallery(Path path):<br>
path 변수에 입력된 경로에 품번을 이름으로 폴더를 하나 더 만들어 작품을 저장합니다.<br>

해야하는 예외처리:<br>
HitomiNotFoundException: 번호에 대한 작품이 없습니다.<br>
TimeOutException: 저장하는데 시간이 너무 오래 걸립니다. 5분이상의 시간동안 저장이 안될시 오류를 리턴합니다.

출력:<br>
10밀리초당 한번 씩 다운로드 완료 된 페이지를 출력합니다.


<hr>
void Hitomi_Download.download_image(String hash, File file):<br>
해시값이 hash인 작품을 file 변수에 들어가 있는 경로와 파일 이름으로 다운로드 합니다.

해야하는 예외처리:<br> 
TooshorthashException: 해시값이 3자리 이하일 때 발생합니다.


<hr>
HashMap &lt;Integer, String&gt; get_gallery_info.get_hash():<br>
작품 번호의 이미지 해시값을 알아냅니다.

리턴값: Integer 값에는 작품의 페이지가, String 값에는 그 페이지의 해시값이 들어갑니다.

해야하는 예외처리:<br>
HitomiNotFoundException: 번호에 대한 작품이 없습니다.



<br><br><br>


