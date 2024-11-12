import React from "react";
import AutoScrollSplide from "../../components/Splide/AutoScrollSplide";
import {
    altPicture,
    cardContent,
    cardIcon,
    cardTitle,
    srcPicture,
} from "../../components/utils/src";
import CardInfor from "../../components/Cards/CardInfor";
import Logo from "../../components/Logo/Logo";

export default function HomePages() {
    return (
        <>
            <div className="container">
                <AutoScrollSplide src={srcPicture} alt={altPicture}/>
                <div className="">
                    <h3 className="mt-40 text-4xl font-semibold text-center">
                        Koi Express là chuyên gia giao hàng của riêng bạn
                    </h3>
                    <h1 className="mt-10 text-xl text-center">
                        Mang đến giải pháp hoàn hảo cho mọi nhu cầu giao hàng của bạn nhờ
                        công nghệ đột phá — tất cả trong một ứng dụng duy nhất.
                    </h1>
                </div>
                {/*  */}
                <div className="flex justify-around mt-28">
                    <CardInfor
                        src={cardIcon}
                        alt={cardTitle}
                        name={cardTitle}
                        desc={cardContent}
                    />
                </div>
                {/*  */}
                <div className="flex justify-between mt-20">
                    <div className="mt-40 ">
                        <h3 className="text-left text-[45px]  font-semibold">
                            Luôn chuyển động cùng cá Koi của bạn
                        </h3>
                        <h1 className="mt-4 text-lg text-left">
                            Chúng tôi cam kết mang đến dịch vụ vận chuyển cá Koi chuyên
                            nghiệp, đảm bảo an toàn và sức khỏe cho từng chú cá của bạn. Với
                            đội ngũ giao hàng tận tâm và phương tiện chuyên dụng, chúng tôi
                            đảm bảo bạn nhận được dịch vụ tốt nhất từ Koi Express, mang lại sự
                            an tâm và tin cậy trên mỗi chuyến hàng.
                        </h1>
                    </div>
                    <img
                        className="ml-6 w-[590px] h-[677px]"
                        src="/src/assets/images/Icons/KoiExpress1.webp"
                        alt="kk"
                    />
                </div>
                <div className="flex justify-between mt-40">
                    <div className="w-56">
                        <h1 className="font-semibold text-[50px] text-blue-500">
                            700,000+
                        </h1>
                        <h1 className="mt-4">Doanh nghiệp/hộ kinh doanh trên toàn quốc</h1>
                    </div>
                    <div className="w-56">
                        <h1 className="font-semibold text-[50px] text-blue-500">
                            2,500,000+
                        </h1>
                        <h1 className="mt-4">Khách hàng tin tưởng sử dụng dịch vụ</h1>
                    </div>
                    <div className="w-56">
                        <h1 className="font-semibold text-[50px] text-blue-500">20</h1>
                        <h1 className="mt-4">Tỉnh thành có sự hiện diện của Koi Express</h1>
                    </div>
                    <div className="w-56">
                        <h1 className="font-semibold text-[50px] text-blue-500">200,000</h1>
                        <h1 className="mt-4">Điểm giao thành công mỗi ngày</h1>
                    </div>
                </div>
                {/*tich hop app  */}
                <div className="flex justify-between mt-20">
                    <img
                        className="mr-6 w-[670px] h-[677] object-contain"
                        src="https://mpos.vn/public/media?fpath=MjAyMTAyMjYtQURNSU4tbXBvczoxMDAwLTAtODAweDUwMF8xMV8=.png"
                        alt="kk"
                    />
                    <div className="mt-20 ">
                        <h3 className="text-left text-[40px]  font-semibold">
                            Tích hợp mạnh mẽ để tiến về phía trước
                        </h3>
                        <h1 className="mt-4 text-lg text-left">
                            Tối ưu quy trình hiện hành và đưa doanh nghiệp của bạn lên một tầm
                            cao mới. Quá trình tích hợp không thể dễ dàng hơn khi đã có chúng
                            tôi hỗ trợ bạn tối đa.
                        </h1>
                    </div>
                </div>
                {/*  */}
                <div>
                    <Logo/>
                </div>
                {/*  */}
            </div>
        </>
    );
}
