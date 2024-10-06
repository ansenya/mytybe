package utils

import (
	"log"
	"os/exec"
)

func ProcessVideo(path string) {
	cmd := exec.Command("sh", "script.sh", path)

	output, err := cmd.CombinedOutput()
	if err != nil {
		log.Println(err)
		return
	}
	log.Println(string(output))

	cmd = exec.Command("rm", path)
	output, err = cmd.CombinedOutput()
	if err != nil {
		log.Println(err)
		return
	}
}
