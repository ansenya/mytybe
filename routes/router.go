package routes

import (
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"storage/controllers"
)

func SetupRouter() *gin.Engine {
	router := gin.Default()
	router.Use(cors.Default())

	router.POST("/upload", controllers.UploadChunkHandler)
	router.GET("/:uuid", controllers.ImagesHandler)

	return router
}
