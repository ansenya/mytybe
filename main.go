package main

import (
	"fmt"
	"os"
	"storage/controllers"
	"storage/routes"
)

func main() {
	err := os.MkdirAll(controllers.DATA_DIR, os.ModePerm)
	if err != nil {
		panic("Failed to create upload directory")
	}

	router := routes.SetupRouter()

	fmt.Println("Server started at :8080")
	router.Run(":8080")
}
