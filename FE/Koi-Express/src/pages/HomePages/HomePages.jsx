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
        <AutoScrollSplide src={srcPicture} alt={altPicture} />
        <div className="">
          <h3 className="mt-40 text-center font-semibold text-4xl">
            Koi Express là chuyên gia giao hàng của riêng bạn
          </h3>
          <h1 className="text-center mt-10 text-xl">
            Mang đến giải pháp hoàn hảo cho mọi nhu cầu giao hàng của bạn nhờ
            công nghệ đột phá — tất cả trong một ứng dụng duy nhất.
          </h1>
        </div>
        {/*  */}
        <div className="flex mt-28 justify-around">
          <CardInfor
            src={cardIcon}
            alt={cardTitle}
            name={cardTitle}
            desc={cardContent}
          />
        </div>
        {/*  */}
        <div className="mt-20 flex justify-between">
          <div className="mt-40 ">
            <h3 className="text-left text-[45px]  font-semibold">
              Luôn chuyển động cùng bạn, dù bạn là ai
            </h3>
            <h1 className="mt-4 text-left  text-lg">
              Chúng tôi mang đến cho doanh nghiệp của bạn và khách hàng những
              trải nghiệm hài lòng nhất về thời gian, chi phí và sự an tâm trên
              mỗi chuyến hàng.
            </h1>
            <button className="mt-5 text-left bg-amber-500 border rounded px-8 py-5">
              <span className="font-semibold">Khám phá thêm</span>
            </button>
          </div>
          <img
            className="ml-6 w-[590px] h-[677px]"
            src="https://www.ahamove.com/_next/image?url=%2Fstatic%2Fimages%2Fhome%2Fmerchant.webp&w=640&q=75"
            alt="kk"
          />
        </div>
        <div className="mt-40 flex justify-between">
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
        <div className="mt-20 flex justify-between">
          <img
            className="mr-6 w-[670px] h-[677] object-contain"
            src="https://mpos.vn/public/media?fpath=MjAyMTAyMjYtQURNSU4tbXBvczoxMDAwLTAtODAweDUwMF8xMV8=.png"
            alt="kk"
          />
          <div className="mt-20 ">
            <h3 className="text-left text-[40px]  font-semibold">
              Tích hợp mạnh mẽ để tiến về phía trước
            </h3>
            <h1 className="mt-4 text-left  text-lg">
              Tối ưu quy trình hiện hành và đưa doanh nghiệp của bạn lên một tầm
              cao mới. Quá trình tích hợp không thể dễ dàng hơn khi đã có chúng
              tôi hỗ trợ bạn tối đa.
            </h1>
            <button className="mt-5 text-left bg-amber-500 border rounded px-8 py-5">
              <span className="font-semibold">Tìm hiểu thêm</span>
            </button>
          </div>
        </div>
        {/*  */}
        <div>
          <Logo />
        </div>
        {/*  */}
      </div>
    </>
  );
}
