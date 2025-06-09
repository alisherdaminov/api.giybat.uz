Giybat.uz — Help Guide

Version: 1.0 – June 9, 2025

Welcome to Giybat.uz — your digital space to share stories, moments, and hot takes with the world. 
Think of it as Instagram with a gossip twist — where everyone has a voice and followers are always curious. 
This guide will help you get started, post confidently, and connect with the community in no time.

🚀 1. Getting Started
📝 Create Your Account

Open the app and tap Sign Up.
Fill in your username, email, and password.
Upload a profile picture, write a bio (optional), and you're ready to go!

🔐 Log In
Tap Login on the home screen.
Enter your username/email and password.
Boom — you're in! Your session is secured using encrypted tokens.

🤔 Forgot your password? Tap Forgot Password to reset via email.
✨ Set Up Your Profile
Upload a cool profile pic
Add location & links (if you want)
Set your profile to private if you're feeling exclusive 😉

🔐 2. Authentication (How You Stay Logged In)

We use secure JWT tokens under the hood.
Token
Lifespan
Stored In
Access
API Login Example
POST /api/auth/login
Content-Type: application/json
{
"username": "giybatchi23",
"password": "P@ssw0rd!"
}
🚀 Tokens are handled automatically by the frontend (e.g. using Axios interceptors).

✅ 3. Authorization (Who Can Do What)

USER
Post, like, comment, follow
ADMIN
Moderate users/posts, delete content

Java Example:
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> deleteAnyPost(Long postId) {
postService.delete(postId);
return ResponseEntity.ok().build();
}

✍️ 4. Creating a Post

Pick a photo/video or take one live.
Add a caption, location, and hashtags.
Hit Share and voilà — it's live!
💻 With the API

POST /api/posts
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
(image + caption)

Response:

{
"id": "3f9d7564-12cf-4207-aba6-36c4f3e8d474",
"title": "Todays meetings ",
"photo": {
"photoId": "1b3e1fc4-e67f-44ca-8903-2bc2c532b2f6.jpg",
"url": "http://localhost:8080/attach/download/1b3e1fc4-e67f-44ca-8903-2bc2c532b2f6.jpg"
},
"createdDate": "2025-06-09T11:09:51.278039",
"status": "ACTIVE"
}

Discover trending posts, tags, and users

🛡️ 6. Security & Privacy

2FA login via SMS or Authenticator apps

HTTPS for all traffic, AES-256 encrypted data

Content filters for inappropriate material

❓ 7. FAQ (Quick Answers)

Forgot password?

Use Forgot Password to reset via email.
Delete a post?
Go to your post → tap ⋯ → select Delete.

💬 8. Need Help?

Telegram: @mr_daminov
✍️ Last updated: June 9, 2025. Have suggestions or found a bug? Let us know!

