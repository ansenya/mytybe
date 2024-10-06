package controllers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strings"
)

func SegmentHandler(c *gin.Context) {
	videoName := c.Param("video")
	quality := c.Query("quality")

	if videoName == "" {
		c.JSON(400, gin.H{"message": "missing video name"})
		return
	}
	if strings.Contains(videoName, ".m3u8") && quality == "" {
		c.JSON(400, gin.H{"message": "quality required for .m3u8 files"})
		return
	}

	var videoPath string
	if quality != "" {
		videoPath = filepath.Join(VIDEOS_DIR, fmt.Sprintf("%s_%s", quality, videoName))
	} else {
		videoPath = filepath.Join(VIDEOS_DIR, videoName)
	}

	log.Printf("Serving video from path: %s", videoPath)

	if _, err := os.Stat(videoPath); os.IsNotExist(err) {
		c.JSON(http.StatusNotFound, gin.H{"message": "video not found"})
		return
	}

	c.Header("Content-Type", "application/vnd.apple.mpegurl")
	c.File(videoPath)
}
