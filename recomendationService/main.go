package main

import (
	"fmt"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"time"
)

type UserModel struct {
	ID       int64 `gorm:"primary_key"`
	Username string
	//Password string
	Name    string
	Surname string
	Sex     string
	//Pfp            ImageModel `gorm:"ForeignKey:ImageID"`
	Age int
	//Role string
	//Country CountryModel
	ChannelsPage []ChannelModel `gorm:"ForeignKey:UserID"`
	//Subscriptions  []ChannelModel  `gorm:"many2many:subscriptions"`
	//Comments       []CommentModel  `gorm:"ForeignKey:UserID"`
	//LikedVideos    []VideoModel    `gorm:"many2many:videos_liked"`
	//DislikedVideos []VideoModel    `gorm:"many2many:videos_disliked"`
	//Playlists      []PlaylistModel `gorm:"ForeignKey:OwnerID"`
	//Created time.Time
	//Updated time.Time
	//Deleted bool `gorm:"column:deleted"`
	//Locked  bool
}

type ImageModel struct {
	ID      int64 `gorm:"primary_key"`
	Path    string
	Type    ImageType `gorm:"ForeignKey:TypeID"`
	Created time.Time
	Updated time.Time
	Deleted bool
}

type CountryModel struct {
	ID      int64 `gorm:"primary_key"`
	Country string
	UsersPage   []UserModel `gorm:"ForeignKey:CountryID"`
}

type ChannelModel struct {
	ID int64 `gorm:"primary_key"`
	//Name string
	//VideosAmount int
	// FollowersAmount можно вычислять на лету
	//Pfp       ImageModel   `gorm:"ForeignKey:PfpID"`
	//VideosPage    []VideoModel `gorm:"ForeignKey:ChannelID"`
	//Posts     []PostModel  `gorm:"ForeignKey:ChannelID"`
	UserPage UserModel `gorm:"ForeignKey:UserID"`
	//Followers []UserModel  `gorm:"many2many:subscriptions"`
	//Deleted   bool
	//Created   time.Time
	//Updated   time.Time
}

type CommentModel struct {
	ID      int64 `gorm:"primary_key"`
	Text    string
	Deleted bool
	Video   VideoModel `gorm:"ForeignKey:VideoID"`
	UserPage    UserModel  `gorm:"ForeignKey:UserID"`
	Created time.Time
	Updated time.Time
}

type VideoModel struct {
	ID             int64 `gorm:"primary_key"`
	Name           string
	Description    string
	Duration       int64
	Views          int64
	Thumbnail      ImageModel     `gorm:"ForeignKey:ThumbnailID"`
	Comments       []CommentModel `gorm:"ForeignKey:VideoID"`
	ChannelPage        ChannelModel   `gorm:"ForeignKey:ChannelID"`
	Category       CategoryModel  `gorm:"ForeignKey:CategoryID"`
	Path           string
	Tags           []TagModel      `gorm:"many2many:videos_tags"`
	Playlists      []PlaylistModel `gorm:"many2many:videos_playlists"`
	LikedByUser    []UserModel     `gorm:"many2many:liked_videos"`
	DislikedByUser []UserModel     `gorm:"many2many:disliked_videos"`
	VidUUID        string
	Processed      bool
	Explicit       bool
	Deleted        bool
	Stream         bool
	Created        time.Time
	Updated        time.Time
}

type PlaylistModel struct {
	ID     int64        `gorm:"primary_key"`
	Owner  UserModel    `gorm:"ForeignKey:OwnerID"`
	VideosPage []VideoModel `gorm:"many2many:videos_playlists"`
}

type TagModel struct {
	ID  int64  `gorm:"primary_key"`
	Tag string `gorm:"column:tag"`
}

type CategoryModel struct {
	ID       int64 `gorm:"primary_key"`
	Category string
	Items    []VideoModel `gorm:"ForeignKey:CategoryID"`
}

type ImageType struct {
	ID   int64 `gorm:"primary_key"`
	Type string
}

type PostModel struct {
	ID      int64 `gorm:"primary_key"`
	Title   string
	Content string
	Images  []ImageModel `gorm:"ForeignKey:PostID"`
	Polls   PollModel    `gorm:"ForeignKey:PostID"`
}

type PollModel struct {
	ID       int64 `gorm:"primary_key"`
	Question string
	Elements []PollElement `gorm:"ForeignKey:PollID"`
}

type PollElement struct {
	ID     int64 `gorm:"primary_key"`
	Answer string
	UserPage   UserModel `gorm:"ForeignKey:UserID"`
}

func main() {
	dsn := "newuser:@tcp(localhost:3306)/tube"

	db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{})
	if err != nil {
		panic("Failed to connect to the database")
	}

	var res []UserModel
	db.Raw("SELECT * FROM users ").Scan(&res)

	fmt.Println(res)
}
