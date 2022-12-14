import { BsShare } from "react-icons/bs";
import { FiShare } from "react-icons/fi";
import { useRouter } from "next/router";
import { follow } from "../../../api/profile";
import Image from "next/image";

import { Toast } from "../../atoms/common/Toast";

function ProfileInfo({ userInfo, setUserInfo }) {
  // 로그인했고, 방문한 페이지의 url과 username이 같은 경우를 찾기 위해
  const router = useRouter();

  // 클립보드에 URL 복사하기
  const copyURL = async () => {
    const url = window.location.href;
    // console.log(url);
    await navigator.clipboard.writeText(url);
    // alert("프로필이 복사되었습니다.");
    Toast.fire({
      customClass: {
        title: "toast-title",
      },
      icon: "success",
      title: "프로필이 복사되었습니다.",
      timer: 1500,
    });
  };

  const success = () => {
    const tempInfo = { ...userInfo };
    tempInfo.user.isFollow = !userInfo.user.isFollow;
    if (tempInfo.user.isFollow) {
      tempInfo.user.followerCount += 1;
      setUserInfo(tempInfo);
    } else {
      tempInfo.user.followerCount -= 1;
      setUserInfo(tempInfo);
    }
  };
  const fail = (err) => console.log(err);

  // 팔로우버튼 클릭
  const onClickFollow = () => {
    const credential = {
      isFollow: !userInfo.user.isFollow,
      userName: router.query.username,
    };
    // console.log(credential);
    follow(credential, success, fail);
  };

  return (
    <div className="profile-head grid grid-cols-5 mt-3">
      {/* 좌측 */}
      <div className="profile-img col-span-2 flex justify-center mx-6">
        <div className="img-box rounded-full aspect-square w-4/5 my-6 relative">
          <Image
            src={userInfo.user.userImage}
            alt={`${userInfo.user.userName} 프로필 이미지입니다.`}
            layout="fill"
            objectFit="cover"
            className="rounded-full"
            priority
          />
          {/* <img
            src={userInfo.user.userImage}
            alt={`${userInfo.user.userName} 프로필 이미지입니다.`}
            className="w-full h-full object-cover rounded-full"
          /> */}
        </div>
      </div>
      {/* 우측 */}
      <div className="profile-info col-span-3 grid content-center">
        <div className="profile-info-box">
          <div className="box-username font-sans font-bold flex items-center flex-start my-1">
            {/* <div className="box-username font-sans font-bold flex flex-start my-1"> */}
            {/* {username} */}
            <span className="mr-2.5 text-font1">{userInfo.user.userName}</span>
            {/* onClick */}
            {/* <span className="grid content-center"> */}
            <FiShare
              size={15}
              className="icon-share cursor-pointer text-font4 mb-1"
              onClick={copyURL}
            />
            {/* </span> */}
          </div>
          {/* onClick */}
          {/* numFollow */}
          <div className="box-follow-info font-sanslight text-xs text-font2 my-0.5">
            <span
              className="cursor-pointer"
              onClick={() => {
                router.push(`/profile/follow/${router.query.username}`);
              }}
            >
              팔로워 {userInfo.user.followerCount} | 팔로잉{" "}
              {userInfo.user.followingCount}
            </span>
          </div>
          {/* Link */}
          {userInfo.isMe ? (
            <div className="box-btns font-sanslight text-xs text-sub2 underline underline-offset-2 my-1">
              <span
                className="cursor-pointer"
                onClick={() => {
                  router.push(`/profile/edit/${router.query.username}`);
                }}
              >
                프로필 수정
              </span>
            </div>
          ) : userInfo.user.isFollow ? (
            <div className="box-btns font-sansultralight text-xs my-0.5 w-1/4">
              <button
                onClick={onClickFollow}
                className="btn-follow bg-main p-1 rounded-md text-white hover:bg-sub1 hover:transition-all w-full"
              >
                팔로잉
              </button>
            </div>
          ) : (
            <div className="box-btns font-sansultralight text-xs my-0.5 w-1/4">
              <button
                onClick={onClickFollow}
                className="btn-follow bg-sub1 p-1 rounded-md text-white hover:bg-main hover:transition-all w-full"
              >
                팔로우
              </button>
            </div>
          )}
        </div>
      </div>
      <style jsx>{`
        .icon-share {
        }
      `}</style>
    </div>
  );
}

export default ProfileInfo;
