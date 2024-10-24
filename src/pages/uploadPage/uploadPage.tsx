import React, {useState} from 'react';
import './uploadPage.scss';
import './uploadPage.module.scss';
import {useCreateVideoEntityMutation} from "../../store/api/serverApi";

const UploadPage: React.FC = () => {
    const [videoFile, setVideoFile] = useState<File | null>(null);
    const [imageFile, setImageFile] = useState<File | null>(null);
    const [isUploading, setIsUploading] = useState(false);
    const [progress, setProgress] = useState(0);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [videoId, setVideoId] = useState<string | null>(null); // Store video ID
    const [createVideoEntity, { isLoading }] = useCreateVideoEntityMutation();

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0] || null;
        if (e.target.id === "video-upload") {
            setVideoFile(file);
        } else if (e.target.id === "image_uploads") {
            setImageFile(file);
        }
    };

    // Create video entry on the server
    const handleCreateVideo = async () => {
        const channelId = "1"; // Specify your channel ID
        const videoData = { title, description, channelId };

        try {
            const response = await createVideoEntity(videoData).unwrap();
            const video = response[0]
            const uuid = response[1].uuid
            console.log(video)
            console.log(uuid)

            await uploadFile(videoFile!, uuid)
            // You might want to handle the response here, such as setting the video ID
        } catch (error) {
            console.error('Failed to create video metadata:', error);
            // Handle error accordingly
        }
    };

    // Upload video chunks to the server
    const uploadFile = async (file: File, uuid: string) => {
        const chunkSize = 1024 * 1024; // 1MB chunks
        const totalChunks = Math.ceil(file.size / chunkSize);
        setIsUploading(true);
        setProgress(0);

        for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
            const start = chunkIndex * chunkSize;
            const end = Math.min(start + chunkSize, file.size);
            const chunk = file.slice(start, end);
            const formData = new FormData();
            formData.append('file', chunk);
            formData.append('chunkIndex', chunkIndex.toString());
            formData.append('totalChunks', totalChunks.toString());
            formData.append('fileName', uuid);
            formData.append('videoId', videoId || ''); // Send video ID for associating the chunks

            try {
                const response = await fetch('http://localhost:8080/videos/upload', {
                    method: 'POST',
                    body: formData,
                });

                if (!response.ok) {
                    throw new Error('Upload error');
                }

                const newProgress = Math.round(((chunkIndex + 1) / totalChunks) * 100);
                setProgress(newProgress); // Update progress bar

            } catch (error) {
                console.error('Upload interrupted:', error);
                setIsUploading(false);
                return;
            }
        }

        console.log('File successfully uploaded');
        setIsUploading(false);
    };

    // Handle the create video and upload steps
    const handleUpload = async () => {
        await handleCreateVideo(); // First, create the video

        // if (!videoId && videoFile) {
        //   await createVideo(); // First, create the video
        // }

        // if (videoFile && videoId) {
        //   uploadFile(videoFile); // Then upload the video file chunks
        // }
    };

    return (
        <div className="video-upload">
            <h1>Upload Video</h1>

            {/* Video creation form */}
            <div className="create-video-form">
                <input
                    type="text"
                    placeholder="Video Title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
                <textarea
                    placeholder="Video Description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
            </div>

            <div className="forms">
                {/* Video file input */}
                <label htmlFor="video-upload" className="custom-file-upload">
                    {videoFile ? videoFile.name : 'Upload Video'}
                </label>
                <input
                    type="file"
                    id="video-upload"
                    accept="video/*"
                    onChange={handleFileChange}
                    style={{display: 'none'}}
                />

                {/* Image file input */}
                <label htmlFor="image_uploads" className="custom-file-upload">
                    {imageFile ? imageFile.name : 'Upload Image'}
                </label>
                <input
                    type="file"
                    id="image_uploads"
                    name="image_uploads"
                    accept=".jpg, .jpeg, .png"
                    onChange={handleFileChange}
                    style={{display: 'none'}}
                />
            </div>

            {/* Upload button */}
            <button
                onClick={handleUpload}
                disabled={isUploading || !videoFile || !title || !description}
            >
                {isUploading ? 'Uploading...' : 'Create and Upload'}
            </button>

            {/* Progress bar */}
            {isUploading && (
                <div className="progress-bar-container">
                    <div className="progress-bar" style={{width: `${progress}%`}}/>
                    <div className="progress-text">{progress}% Uploaded</div>
                </div>
            )}
        </div>
    );
};

export default UploadPage;
