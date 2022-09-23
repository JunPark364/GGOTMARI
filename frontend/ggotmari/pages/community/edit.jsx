import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { IoCameraOutline, IoRefreshOutline } from "react-icons/io5";
import { getFlowerKind } from "../../api/community";
import FlowerTag from "../../components/atoms/common/FlowerTag";

function EditArticle() {
  const router = useRouter();
  const [title, setTitle] = useState("");
  const [flowerTags, setFlowerTags] = useState([]);
  const [content, setContent] = useState("");
  const [tagSearch, setTagSearch] = useState("");
  const [filteredList, setFilteredList] = useState([]);
  const [flowerKindList, setFlowerKindList] = useState([]);
  const [dropDownOpen, setDropDownOpen] = useState(false);
  const [imagesPreview, setImagesPreview] = useState([]);

  useEffect(() => {
    getFlowerKind(
      (res) => setFlowerKindList(res.data.subjects),
      (error) => {
        console.log(error);
      },
    );
  }, []);

  useEffect(() => {
    setFilteredList(flowerKindList);
  }, [flowerKindList]);

  useEffect(() => {
    setFilteredList(
      flowerKindList.filter((flowerKind) =>
        flowerKind.subjectName.startsWith(tagSearch),
      ),
    );
  }, [tagSearch]);

  const addFlowerTag = (e) => {
    const newFlower = e.target.innerHTML;
    if (!flowerTags.includes(newFlower)) {
      setFlowerTags([...flowerTags, e.target.innerHTML]);
    }
  };

  const removeFlowerTag = (tag) => {
    setFlowerTags(flowerTags.filter((flower) => flower != tag));
  };

  const handleFlowerSearchChange = (e) => {
    setTagSearch(e.target.value);
  };

  const handleImgUpload = (e) => {
    console.log(e.target.files);
    const fileArr = e.target.files;
    const fileURLs = [];
    [...fileArr].forEach((file, idx) => {
      const reader = new FileReader();
      reader.onload = () => {
        fileURLs[idx] = reader.result;
        setImagesPreview([...fileURLs]);
      };
      reader.readAsDataURL(file);
    });
  };

  const handleTitleChange = (e) => {
    setTitle(e.target.value);
  };

  const handleContentChange = (e) => {
    setContent(e.target.value);
  };

  const handleArticleSubmit = (e) => {
    e.preventDefault();
    console.log(title);
    console.log(content);
    console.log();
    console.log(flowerTags);
  };

  return (
    <div className="flex flex-col items-center w-screen">
      <div className="w-full aspect-square bg-main">
        <div className="carousel w-full h-full">
          {imagesPreview.length > 0
            ? imagesPreview.map((imgSrc, idx) => (
                <div className="carousel-item relative w-full h-full" key={idx}>
                  <img src={imgSrc} className="w-full h-full object-cover" />
                </div>
              ))
            : "사진 없음"}
        </div>
      </div>
      <div className="flex flex-row space-x-3 justify-center my-3">
        <label className="inline text-font2 cursor-pointer" htmlFor="flowerImg">
          <IoCameraOutline className="inline" /> 사진 첨부
        </label>
        <input
          type="file"
          accept="image/*"
          className="absolute w-0 h-0 p-0 overflow-hidden border-0"
          id="flowerImg"
          multiple
          onChange={handleImgUpload}
        />
        <p> | </p>
        <div
          className="inline text-font2 cursor-pointer"
          onClick={() => setImagesPreview([])}
        >
          <IoRefreshOutline className="inline" /> 초기화
        </div>
      </div>
      <div className="w-full p-3">
        <form
          className="flex flex-col w-full space-y-4 font-sans text-font2"
          onSubmit={handleArticleSubmit}
        >
          <label htmlFor="articleTitle" className="pl-2 text-sm">
            글 제목
          </label>
          <input
            type="text"
            id="articleTitle"
            className="shadow-md w-full text-sm focus:outline-none px-3 py-2"
            placeholder="제목을 입력하세요"
            onFocus={() => setDropDownOpen(false)}
            onChange={handleTitleChange}
          />
          <label htmlFor="flowerTags" className="pl-2 text-sm">
            꽃 태그
          </label>
          <div className="w-full shadow-md">
            <div>
              <div className="flex flex-row flex-wrap px-5 py-3">
                {flowerTags?.map((tag) => (
                  <FlowerTag
                    flowerName={tag}
                    key={tag}
                    isRemovable={true}
                    onClick={() => removeFlowerTag(tag)}
                  />
                ))}
              </div>
              <hr />
            </div>

            <input
              type="text"
              className="w-full text-sm focus:outline-none p-3"
              placeholder="꽃을 검색하세요"
              onClick={() => setDropDownOpen(true)}
              onChange={handleFlowerSearchChange}
            />
            <hr />
            <div
              className={
                "max-h-32 z-10 overflow-auto " +
                (dropDownOpen ? "relative" : "hidden")
              }
            >
              <div className="">
                {filteredList.map((flower, idx) => (
                  <div
                    className="p-2 font-sans hover:bg-font3"
                    onClick={addFlowerTag}
                    key={idx}
                  >
                    {flower.subjectName}
                  </div>
                ))}
              </div>
            </div>
          </div>
          <label htmlFor="articleContent" className="pl-2 text-sm">
            내용
          </label>
          <textarea
            id="articleContent"
            rows="5"
            className="shadow-md w-full text-sm focus:outline-none p-3"
            placeholder="내용을 입력하세요"
            onFocus={() => setDropDownOpen(false)}
            value={content}
            onChange={handleContentChange}
          ></textarea>
          <input type="submit" />
        </form>
      </div>
      <div className="h-14"></div>
    </div>
  );
}

export default EditArticle;
