package controllers

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"os"
	"path/filepath"
)

func ImagesHandler(c *gin.Context) {
	imageUuid := c.Param("uuid")

	if imageUuid == "" {
		c.JSON(400, gin.H{"message": "Missing image uuid in the request. Please include the video identifier."})
		return
	}

	var imagePath string
	imagePath = filepath.Join(DATA_DIR, imageUuid+".webp")
	c.Header("Content-Type", "image/webp")

	if _, err := os.Stat(imagePath); os.IsNotExist(err) {
		c.JSON(http.StatusNotFound, gin.H{"message": "Image not found. Please verify the video name and quality."})
		return
	}

	c.File(imagePath)
}
