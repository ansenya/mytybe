package main

import (
	"fmt"
	"log"
	"net/http"
	"path/filepath"
)

// Функция для установки CORS-заголовков
func setCORSHeaders(w http.ResponseWriter) {
	w.Header().Set("Access-Control-Allow-Origin", "*") // Разрешить запросы с любого источника
	w.Header().Set("Access-Control-Allow-Methods", "GET, OPTIONS")
	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
}

// Обработчик для master playlist (.m3u8)
func masterPlaylistHandler(w http.ResponseWriter, r *http.Request) {
	setCORSHeaders(w) // Установим CORS-заголовки
	playlistPath := filepath.Join("/Users/senya/GolandProjects/spot/videos", filepath.Base(r.URL.Path))
	fmt.Println(playlistPath)
	http.ServeFile(w, r, playlistPath)
}

// Обработчик для сегментов (.ts)
func segmentHandler(w http.ResponseWriter, r *http.Request) {
	setCORSHeaders(w) // Установим CORS-заголовки
	segmentPath := filepath.Join("/Users/senya/GolandProjects/spot/videos", filepath.Base(r.URL.Path))
	http.ServeFile(w, r, segmentPath)
}

func loggingMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		log.Printf("Received %s request for %s from %s", r.Method, r.URL.Path, r.RemoteAddr)
		next.ServeHTTP(w, r)
	})
}

func main() {
	mux := http.NewServeMux()

	mux.HandleFunc("/hls/",
		func(w http.ResponseWriter, r *http.Request) {
			// Для preflight-запросов (OPTIONS) обработка CORS
			if r.Method == http.MethodOptions {
				setCORSHeaders(w)
				return
			}

			ext := filepath.Ext(r.URL.Path)
			switch ext {
			case ".m3u8":
				masterPlaylistHandler(w, r)
			case ".ts":
				segmentHandler(w, r)
			default:
				http.NotFound(w, r)
			}
		})

	//http.HandleFunc("/hls/", func(w http.ResponseWriter, r *http.Request) {
	//	// Для preflight-запросов (OPTIONS) обработка CORS
	//	if r.Method == http.MethodOptions {
	//		setCORSHeaders(w)
	//		return
	//	}
	//
	//	ext := filepath.Ext(r.URL.Path)
	//	switch ext {
	//	case ".m3u8":
	//		masterPlaylistHandler(w, r)
	//	case ".ts":
	//		segmentHandler(w, r)
	//	default:
	//		http.NotFound(w, r)
	//	}
	//})

	loggedRouter := loggingMiddleware(mux)

	fmt.Println("HLS server running on :8080")
	log.Fatal(http.ListenAndServe(":8080", loggedRouter))
}
