package main

import (
	"fmt"
)

// Video структура представляет видео на платформе
type Video struct {
	ID          int
	Title       string
	Description string
	Tags        []string
}

// UserProfile структура представляет профиль пользователя
type UserProfile struct {
	Gender   string
	Age      int
	Liked    []int
	Disliked []int
	Tags     []string
}

// RecommendVideos функция для рекомендации видео на основе контентной фильтрации
func RecommendVideos(userProfile UserProfile, videos []Video, numRecommendations int) []Video {
	// Создайте карту для хранения сходства между видео и профилем пользователя
	videoSimilarity := make(map[int]float64)

	// Рассчитайте сходство между видео и профилем пользователя
	for _, video := range videos {
		similarity := calculateContentSimilarity(userProfile, video)
		videoSimilarity[video.ID] = similarity
	}

	// Отсортируйте видео по убыванию сходства
	recommendedVideos := make([]Video, 0)
	for i := 0; i < numRecommendations; i++ {
		maxSimilarity := -1.0
		recommendedVideoID := -1
		for videoID, similarity := range videoSimilarity {
			if similarity > maxSimilarity {
				maxSimilarity = similarity
				recommendedVideoID = videoID
			}
		}
		if recommendedVideoID == -1 {
			break
		}
		recommendedVideos = append(recommendedVideos, videos[recommendedVideoID-1])
		delete(videoSimilarity, recommendedVideoID)
	}

	return recommendedVideos
}

// Функция для расчета сходства между профилем пользователя и видео
func calculateContentSimilarity(userProfile UserProfile, video Video) float64 {
	// Пример: просто сравниваем количество общих тегов
	commonTags := 0
	for _, userTag := range userProfile.Tags {
		for _, videoTag := range video.Tags {
			if userTag == videoTag {
				commonTags++
			}
		}
	}
	// Вы можете использовать более сложные методы для вычисления сходства
	// на основе контента, такие как TF-IDF или Word2Vec.

	// В этом примере просто возвращаем количество общих тегов.
	return float64(commonTags)
}

func main() {
	// Пример данных: создайте профиль пользователя и видео
	userProfile := UserProfile{
		Gender:   "Male",
		Age:      25,
		Liked:    []int{1, 2, 3},
		Disliked: []int{4, 5},
		Tags:     []string{"funny", "music", "travel", "sports"},
	}

	videos := []Video{
		{1, "Funny Cats", "Cute cats doing funny things", []string{"cats", "funny", "animals"}},
		{2, "Music Video", "Latest music video release", []string{"music", "entertainment"}},
		{3, "Travel Vlog", "Explore the world's most beautiful places", []string{"travel", "adventure", "vacation"}},
		{4, "Cooking Tutorial", "Learn to cook delicious recipes", []string{"cooking", "food", "recipes"}},
		{5, "Sports Highlights", "Exciting sports moments", []string{"sports", "highlights", "athletics"}},
	}

	// Рекомендация видео
	recommendedVideos := RecommendVideos(userProfile, videos, 3)

	fmt.Println("Рекомендуемые видео:")
	for _, video := range recommendedVideos {
		fmt.Printf("ID: %d, Заголовок: %s\n", video.ID, video.Title)
	}
}
