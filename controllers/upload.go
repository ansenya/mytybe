package controllers

import (
	"github.com/gin-gonic/gin"
	"io"
	"os"
	"path/filepath"
	"storage/utils"
	"strconv"
)

const VIDEOS_DIR = "videos"

func UploadChunkHandler(c *gin.Context) {
	chunkIndex, err := strconv.Atoi(c.PostForm("chunkIndex"))
	if err != nil {
		c.JSON(400, gin.H{"error": "Invalid chunk index"})
		return
	}

	totalChunks, err := strconv.Atoi(c.PostForm("totalChunks"))
	if err != nil {
		c.JSON(400, gin.H{"error": "Invalid total chunks"})
		return
	}

	fileName := c.PostForm("fileName")

	file, err := c.FormFile("file")
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to get file"})
		return
	}

	partFilePath := filepath.Join(VIDEOS_DIR, fileName+".part")
	outFile, err := os.OpenFile(partFilePath, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to open part file"})
		return
	}
	defer outFile.Close()

	src, err := file.Open()
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to open uploaded file"})
		return
	}
	defer src.Close()

	_, err = io.Copy(outFile, src)
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to write chunk"})
		return
	}

	if chunkIndex == totalChunks-1 {
		finalFilePath := filepath.Join(VIDEOS_DIR, fileName)
		err := os.Rename(partFilePath, finalFilePath)
		if err != nil {
			c.JSON(500, gin.H{"error": "Failed to finalize file"})
			return
		}
		go utils.ProcessVideo(finalFilePath)
		c.JSON(200, gin.H{"status": "complete"})
	} else {
		c.JSON(200, gin.H{"status": "incomplete", "chunkIndex": chunkIndex})
	}
}
