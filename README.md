# AIChatBOT-TCPSocket
## Yêu cầu về chức năng phía client (phải có GUI):
* Sử dụng API của Simsimi (hoặc tự chọn lựa một công cụ khác) để xây dựng AI chatbot. Đồng
thời với chat tự động, chatbot sẽ trả lời thông tin thời tiết, xác định vị trí IP, quét port khi người
dùng đặt câu hỏi, chi tiết như sau:
* Tra cứu thời tiết: gửi yêu cầu là tên một tỉnh/thành phố hoặc địa danh (bằng tiếng Việt).
Kết quả phản hồi là thời tiết ngày hiện tại và một số ngày/tuần (do SV quyết định) kế
tiếp của địa điểm đó.
* Xác định vị trí IP: gửi yêu cầu là 1 địa chỉ IP bất kỳ. Kết quả phản hồi là tọa độ/địa điểm
tương ứng với địa chỉ IP đó. SV tham khảo kết quả từ https://www.iplocation.net/ để
biết các thông tin cần trả về. Nhóm SV có thể sử dụng API có sẵn hoặc trích xuất kết quả
từ một website có công cụ này.
* Quét port: gửi yêu cầu là một địa chỉ IP. Kết quả phản hồi là các port đang mở trong giới
hạn từ port x đến port y (với x, y là dữ liệu người dùng nhập).
* Các chức năng khác nếu có cài đặt sẽ có điểm cộng.

## Yêu cầu về chức năng phía server (không cần GUI):
* Nhận các yêu cầu từ client, sử dụng Java socket kết hợp API hoặc (các tools/ngôn ngữ khác)
để lấy kết quả trả về cho client. Tất cả xử lý phải nằm ở phía server.
## Yêu cầu chung:
* Mã hóa nội dung tin nhắn giữa client – server. Phải sử dụng key khác nhau cho các client.
* Các client phải chạy trên các máy tính khác nhau.
