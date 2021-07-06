# YouCoolMusic

<p align="center"><img width="128" height="128" src="app/src/main/ic_launcher-playstore.png" /></p>

## EN

My analogue is YouTube Music and YouTube Premium.

The app uses the ***YouTube API KEY*** to search for videos on **YouTube**. [References to documentation](https://developers.google.com/youtube/v3/docs/search/list). 
Using this API key occurs in the following lines:

```java
68        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=Музыка&key=[YouTube_API_KEY]");
```
```java
84                        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=" + URLEncoder.encode(name, String.valueOf(StandardCharsets.UTF_8))
85                                + "&key=[YouTube_API_KEY]");
``` 

Link to file from [YouTube_API_KEY](app/src/main/java/com/example/youcoolmusic/Activity/SearchActivity.java).

### Screenshots of the application:

<p>
  <img src="screens/sc1.png" height="500px"/>
  <img src="screens/sc2.png" height="500px"/>
  <img src="screens/sc3.png" height="500px"/>
  <img src="screens/sc4.png" height="500px"/>
</p>

## UA

Мій аналог - YouTube Music та YouTube Premium.

В додатку використовується ***YouTube API KEY*** для пошуку відео в **YouTube**. [Посилання на документацію](https://developers.google.com/youtube/v3/docs/search/list). 
Використання цього API ключа зустрічається таких рядках:

```java
68        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=Музыка&key=[YouTube_API_KEY]");
```
```java
84                        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=" + URLEncoder.encode(name, String.valueOf(StandardCharsets.UTF_8))
85                                + "&key=[YouTube_API_KEY]");
```                                

Посилання на файл із [YouTube_API_KEY](app/src/main/java/com/example/youcoolmusic/Activity/SearchActivity.java).

### Скріншоти додатка:

<p>
  <img src="screens/sc1.png" height="500px"/>
  <img src="screens/sc2.png" height="500px"/>
  <img src="screens/sc3.png" height="500px"/>
  <img src="screens/sc4.png" height="500px"/>
</p>

## Others

### License

```
© 2020, CoolOtaku (ericspz531@gmail.com)
```
