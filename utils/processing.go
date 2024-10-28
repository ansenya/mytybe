package utils

import (
	"log"
	"os/exec"
)

func MakeWebp(path string) {
	cmd := exec.Command("sh", "script.sh", path)

	_, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("failed to convert to webp!", err)
		return
	}

	cmd = exec.Command("rm", path)
	_, err = cmd.CombinedOutput()
	if err != nil {
		log.Println("failed to delete original fail!", err)
		return
	}
}
