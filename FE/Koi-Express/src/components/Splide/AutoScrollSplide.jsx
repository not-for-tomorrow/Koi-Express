import { Splide, SplideSlide } from "@splidejs/react-splide";
import "@splidejs/react-splide/css";
import "./AutoScrollSplide.css";
const AutoScrollSplide = ({ src, alt }) => {
  return (
    <Splide
      options={{
        type: "loop",
        gap: "1rem",
        autoWidth: 1288,
        arrows: true,
        pagination: true,
        perPage: 1,
        autoplay: true,
        interval: 3000,
        pauseOnHover: false,
        resetProgress: false,
        speed: 100,
        breakpoints: {
          640: {
            perPage: 1,
          },
        },
      }}
    >
      {/* SplideSlide items */}
      {src.map((source, index) => (
        <SplideSlide key={index} className="h-[604px] w-[1288px]">
          <img src={source} alt={`${alt} ${index}`} />
        </SplideSlide>
      ))}
    </Splide>
  );
};

export default AutoScrollSplide;
